package com.example.feature.main.chat

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil3.compose.AsyncImage
import com.example.core.design.theme.DanTalkTheme
import com.example.core.ui.components.PhotoViewerDialog
import com.example.core.ui.components.UserDialogInfo
import com.example.core.ui.model.UiMessage
import com.example.feature.main.chat.component.ChatComponent
import com.example.feature.main.chat.model.MessageListItem
import com.example.feature.main.chat.store.ChatStore
import com.example.feature.main.chat.ui.components.BottomChatBar
import com.example.feature.main.chat.ui.components.ChatTopBar
import com.example.feature.main.chat.ui.components.Message
import com.example.feature.main.chat.ui.components.MessagesDate
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun ChatContent(
    component: ChatComponent,
) {
    val state by component.state.collectAsState()

    Content(
        state = state,
        onIntent = component::onIntent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    state: ChatStore.State,
    onIntent: (ChatStore.Intent) -> Unit,
) {
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var recorder by remember { mutableStateOf<MediaRecorder?>(null) }
    var recordedFile by remember { mutableStateOf<File?>(null) }
    var recordingStartedAt by remember { mutableLongStateOf(0L) }
    var recordingSeconds by remember { mutableIntStateOf(0) }
    val isRecording = recorder != null

    var isUserDialogVisible by remember { mutableStateOf(false) }
    var isAttachmentsVisible by remember { mutableStateOf(false) }
    var selectedPhoto by remember { mutableStateOf<UiMessage?>(null) }

    val photoMessages by remember(state.messages) {
        derivedStateOf {
            state.messages
                .filterIsInstance<MessageListItem.MessageItem>()
                .map { it.message }
                .filter { it.isPhoto }
        }
    }

    fun startVoiceRecording() {
        if (isRecording) return

        val file = File(context.cacheDir, "voice_${System.currentTimeMillis()}.m4a")
        val nextRecorder = createVoiceRecorder(context).apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioEncodingBitRate(96_000)
            setAudioSamplingRate(44_100)
            setOutputFile(file.absolutePath)
        }

        runCatching {
            nextRecorder.prepare()
            nextRecorder.start()
        }.onSuccess {
            recorder = nextRecorder
            recordedFile = file
            recordingStartedAt = System.currentTimeMillis()
            recordingSeconds = 0
        }.onFailure {
            Log.e("ChatContent", "Failed to start voice recording", it)
            nextRecorder.release()
            file.delete()
        }
    }

    fun finishVoiceRecording(send: Boolean) {
        val currentRecorder = recorder ?: return
        val file = recordedFile
        val durationMillis = System.currentTimeMillis() - recordingStartedAt
        val stopped = runCatching { currentRecorder.stop() }.isSuccess

        currentRecorder.release()
        recorder = null
        recordedFile = null
        recordingStartedAt = 0L
        recordingSeconds = 0

        if (send && stopped && file != null && file.length() > 0L && durationMillis >= 500L) {
            onIntent(
                ChatStore.Intent.SendVoice(
                    context = context,
                    uri = Uri.fromFile(file),
                    durationMillis = durationMillis,
                    sizeBytes = file.length()
                )
            )
            scope.launch { lazyListState.scrollToItem(0) }
        } else {
            file?.delete()
        }
    }

    val recordPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) startVoiceRecording()
        }

    val photoLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                onIntent(ChatStore.Intent.SendPhoto(context, uri))
                scope.launch { lazyListState.scrollToItem(0) }
            }
        }

    DisposableEffect(Unit) {
        onDispose {
            recorder?.release()
            recordedFile?.delete()
        }
    }

    LaunchedEffect(isRecording, recordingStartedAt) {
        while (isRecording) {
            recordingSeconds = ((System.currentTimeMillis() - recordingStartedAt) / 1000L).toInt()
            delay(300L)
        }
    }

    val firstVisibleDate by remember(state.messages) {
        derivedStateOf {
            when (val firstItem = state.messages.getOrNull(lazyListState.firstVisibleItemIndex)) {
                is MessageListItem.DateItem -> firstItem.date
                is MessageListItem.MessageItem -> firstItem.message.date
                else -> ""
            }
        }
    }

    val isDateVisible by remember(state.messages) {
        derivedStateOf {
            val visibleItems = lazyListState.layoutInfo.visibleItemsInfo
            if (visibleItems.isEmpty()) return@derivedStateOf false
            val alreadyVisible = visibleItems.any {
                val item = state.messages.getOrNull(it.index)
                item is MessageListItem.DateItem && item.date == firstVisibleDate
            }
            !alreadyVisible
        }
    }

    val unreadMessageIds by remember(state.messages) {
        derivedStateOf {
            val visibleIndexes = lazyListState.layoutInfo.visibleItemsInfo.map { it.index }
            if (visibleIndexes.isEmpty()) return@derivedStateOf emptyList()

            val firstVisibleUnreadIndex = visibleIndexes
                .mapNotNull { index ->
                    val item = state.messages.getOrNull(index)
                    if (item is MessageListItem.MessageItem &&
                        !item.message.isCurrentUserMessage &&
                        !item.message.read
                    ) {
                        index
                    } else {
                        null
                    }
                }
                .minOrNull()

            if (firstVisibleUnreadIndex == null) return@derivedStateOf emptyList()

            state.messages
                .drop(firstVisibleUnreadIndex)
                .filterIsInstance<MessageListItem.MessageItem>()
                .filter { !it.message.isCurrentUserMessage && !it.message.read }
                .map { it.message.id }
        }
    }

    LaunchedEffect(unreadMessageIds) {
        if (unreadMessageIds.isEmpty()) return@LaunchedEffect
        onIntent(ChatStore.Intent.ReadMessage(unreadMessageIds))
    }

    if (isAttachmentsVisible) {
        AttachmentsSheet(
            photos = photoMessages,
            onDismiss = { isAttachmentsVisible = false },
            onPhotoClick = {
                selectedPhoto = it
                isAttachmentsVisible = false
            }
        )
    }

    selectedPhoto?.let { photo ->
        PhotoViewerDialog(
            imageUrl = photo.message,
            onDismissRequest = { selectedPhoto = null },
            onDownloadClick = {
                onIntent(ChatStore.Intent.DownloadImage(context = context, url = photo.message))
            }
        )
    }

    Scaffold(
        topBar = {
            ChatTopBar(
                user = state.chat?.user,
                onAvatarClick = { isUserDialogVisible = true },
                navigateBack = { onIntent(ChatStore.Intent.NavigateBack) }
            )
        },
        bottomBar = {
            BottomChatBar(
                message = state.currentMessage,
                isEditing = state.editingMessageId != null,
                isRecording = isRecording,
                recordingSeconds = recordingSeconds,
                replyingToMessage = state.replyingToMessage,
                onMessageChange = { onIntent(ChatStore.Intent.OnMessageChange(it)) },
                cancelEdit = { onIntent(ChatStore.Intent.CancelEditMessage) },
                cancelReply = { onIntent(ChatStore.Intent.CancelReplyMessage) },
                startVoiceRecording = {
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.RECORD_AUDIO
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        startVoiceRecording()
                    } else {
                        recordPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                },
                sendVoiceRecording = { finishVoiceRecording(send = true) },
                cancelVoiceRecording = { finishVoiceRecording(send = false) },
                sendMessage = {
                    onIntent(ChatStore.Intent.SendMessage)
                    scope.launch { lazyListState.scrollToItem(0) }
                },
                sendPhoto = { photoLauncher.launch("image/*") }
            )
        },
        containerColor = DanTalkTheme.colors.altSingleTheme
    ) { contentPadding ->
        if (isUserDialogVisible && state.chat != null) {
            UserDialogInfo(
                onDismissRequest = { isUserDialogVisible = false },
                actionButtonContent = {
                    Text(
                        text = "Посмотреть вложения",
                        fontSize = 14.sp
                    )
                },
                onActionButtonClick = {
                    isUserDialogVisible = false
                    isAttachmentsVisible = true
                },
                onDownloadButtonClick = {
                    onIntent(
                        ChatStore.Intent.DownloadImage(
                            context = context,
                            url = state.chat.user.avatar
                        )
                    )
                },
                user = state.chat.user
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(if (isUserDialogVisible) 3.dp else 0.dp)
                .padding(contentPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            if (state.chat != null) {
                if (state.messages.isEmpty()) {
                    EmptyChatContent()
                } else {
                    LazyColumn(
                        state = lazyListState,
                        reverseLayout = true,
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items(
                            items = state.messages,
                            key = { it.stableKey }
                        ) { item ->
                            when (item) {
                                is MessageListItem.DateItem -> MessagesDate(item.date)
                                is MessageListItem.MessageItem -> Message(
                                    message = item.message,
                                    onReplyClick = {
                                        onIntent(ChatStore.Intent.StartReplyMessage(it))
                                    },
                                    onEditClick = {
                                        onIntent(
                                            ChatStore.Intent.StartEditMessage(
                                                messageId = it.id,
                                                message = it.message
                                            )
                                        )
                                    },
                                    onDeleteClick = {
                                        onIntent(ChatStore.Intent.DeleteMessage(it.id))
                                    },
                                    onPhotoClick = { selectedPhoto = it }
                                )
                            }
                        }
                    }
                }
                if (isDateVisible && firstVisibleDate.isNotEmpty()) {
                    MessagesDate(
                        date = firstVisibleDate,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .background(
                                color = DanTalkTheme.colors.singleTheme.copy(alpha = 0.9f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            } else {
                ChatShimmerContent()
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun AttachmentsSheet(
    photos: List<UiMessage>,
    onDismiss: () -> Unit,
    onPhotoClick: (UiMessage) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = DanTalkTheme.colors.singleTheme,
        contentColor = DanTalkTheme.colors.oppositeTheme
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.PhotoLibrary,
                contentDescription = null,
                tint = DanTalkTheme.colors.main
            )
            Text(
                text = "Вложения",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = DanTalkTheme.colors.oppositeTheme
            )
        }

        if (photos.isEmpty()) {
            Text(
                text = "В этом чате пока нет фото",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 32.dp),
                textAlign = TextAlign.Center,
                color = DanTalkTheme.colors.hint
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp)
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                gridItems(
                    items = photos,
                    key = { it.id }
                ) { photo ->
                    AsyncImage(
                        model = photo.message,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .background(DanTalkTheme.colors.altSingleTheme, RoundedCornerShape(8.dp))
                            .clickable { onPhotoClick(photo) },
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatShimmerContent() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            color = DanTalkTheme.colors.main
        )
    }
}

@Composable
private fun EmptyChatContent() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Еще нет сообщений",
            fontSize = 16.sp,
            color = DanTalkTheme.colors.oppositeTheme
        )
    }
}

@Suppress("DEPRECATION")
private fun createVoiceRecorder(context: Context): MediaRecorder =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        MediaRecorder(context)
    } else {
        MediaRecorder()
    }

private val MessageListItem.stableKey: String
    get() = when (this) {
        is MessageListItem.DateItem -> "date_$date"
        is MessageListItem.MessageItem -> "message_${message.id}"
    }

@Preview
@Composable
private fun Preview() {
    DanTalkTheme {
        Content(
            state = ChatStore.State(),
            onIntent = {}
        )
    }
}
