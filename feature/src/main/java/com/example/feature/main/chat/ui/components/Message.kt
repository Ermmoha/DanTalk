package com.example.feature.main.chat.ui.components

import android.media.MediaPlayer
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Reply
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.size.Size
import com.example.core.design.theme.DanTalkTheme
import com.example.core.ui.model.UiMessage
import com.example.core.util.toDateString

@Composable
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
fun Message(
    message: UiMessage,
    onReplyClick: (UiMessage) -> Unit,
    onEditClick: (UiMessage) -> Unit,
    onDeleteClick: (UiMessage) -> Unit,
    onPhotoClick: (UiMessage) -> Unit
) {
    var showActions by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val arrangement = if (message.isCurrentUserMessage) Arrangement.End else Arrangement.Start
    val paddingValues =
        if (message.isCurrentUserMessage) PaddingValues(start = 60.dp) else PaddingValues(end = 60.dp)
    val tailAlignment =
        if (message.isCurrentUserMessage) Alignment.BottomEnd else Alignment.BottomStart
    val messageShape = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp,
        bottomStart = if (message.isCurrentUserMessage) 16.dp else 0.dp,
        bottomEnd = if (message.isCurrentUserMessage) 0.dp else 16.dp
    )
    val bubbleColor =
        if (message.isCurrentUserMessage) DanTalkTheme.colors.main else DanTalkTheme.colors.singleTheme
    val contentColor =
        if (message.isCurrentUserMessage) Color.White else DanTalkTheme.colors.oppositeTheme
    val canEdit = message.isCurrentUserMessage && !message.isMedia

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(paddingValues)
            .padding(horizontal = 8.dp),
        horizontalArrangement = arrangement,
        verticalAlignment = Alignment.Bottom
    ) {
        Column {
            Box(contentAlignment = tailAlignment) {
                Box(
                    modifier = Modifier
                        .padding(
                            end = if (message.isCurrentUserMessage) 6.dp else 0.dp,
                            start = if (message.isCurrentUserMessage) 0.dp else 6.dp
                        )
                        .combinedClickable(
                            onClick = {
                                if (message.isPhoto) onPhotoClick(message)
                            },
                            onLongClick = { showActions = true }
                        )
                        .background(
                            color = bubbleColor,
                            shape = messageShape
                        )
                        .padding(if (message.isMedia) 6.dp else 10.dp)
                ) {
                    MessageBody(
                        message = message,
                        contentColor = contentColor,
                        onPhotoClick = { onPhotoClick(message) }
                    )
                }
                MessageTail(
                    isCurrentUserMessage = message.isCurrentUserMessage
                )
            }
            Row(
                modifier = Modifier.align(if (message.isCurrentUserMessage) Alignment.End else Alignment.Start),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                if (message.isEdited) {
                    Text(
                        text = "изменено",
                        color = DanTalkTheme.colors.hint,
                        fontSize = 10.sp
                    )
                }
                Text(
                    text = message.time,
                    modifier = Modifier.padding(top = 3.dp),
                    color = DanTalkTheme.colors.hint,
                    fontSize = 10.sp
                )
                if (message.isCurrentUserMessage) {
                    Icon(
                        imageVector = if (!message.isPending) Icons.Default.Check else Icons.Default.Pending,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = if (message.read) DanTalkTheme.colors.main else DanTalkTheme.colors.hint
                    )
                }
            }
        }
    }

    if (showActions) {
        ModalBottomSheet(
            onDismissRequest = { showActions = false },
            containerColor = DanTalkTheme.colors.singleTheme,
            contentColor = DanTalkTheme.colors.oppositeTheme
        ) {
            Text(
                text = "Действия с сообщением",
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                fontWeight = FontWeight.SemiBold,
                color = DanTalkTheme.colors.oppositeTheme
            )
            MessageActionRow(
                icon = Icons.AutoMirrored.Outlined.Reply,
                title = "Ответить",
                tint = DanTalkTheme.colors.main,
                onClick = {
                    showActions = false
                    onReplyClick(message)
                }
            )
            if (canEdit) {
                MessageActionRow(
                    icon = Icons.Outlined.Edit,
                    title = "Редактировать",
                    tint = DanTalkTheme.colors.main,
                    onClick = {
                        showActions = false
                        onEditClick(message)
                    }
                )
            }
            if (message.isCurrentUserMessage) {
                MessageActionRow(
                    icon = Icons.Outlined.Delete,
                    title = "Удалить",
                    tint = DanTalkTheme.colors.red,
                    onClick = {
                        showActions = false
                        showDeleteDialog = true
                    }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Удалить сообщение?") },
            text = { Text("Сообщение исчезнет из этого чата.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteClick(message)
                    }
                ) {
                    Text(
                        text = "Удалить",
                        color = DanTalkTheme.colors.red
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Отмена")
                }
            },
            containerColor = DanTalkTheme.colors.singleTheme,
            titleContentColor = DanTalkTheme.colors.oppositeTheme,
            textContentColor = DanTalkTheme.colors.hint
        )
    }
}

@Composable
private fun MessageActionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    tint: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint
        )
        Text(
            text = title,
            color = DanTalkTheme.colors.oppositeTheme,
            fontSize = 16.sp
        )
    }
}

@Composable
private fun MessageBody(
    message: UiMessage,
    contentColor: Color,
    onPhotoClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        if (message.replyToText.isNotBlank()) {
            ReplyQuote(
                sender = message.replyToSender,
                text = message.replyToText,
                contentColor = contentColor
            )
        }
        when {
            message.isPhoto -> MessagePhoto(message.message, contentColor, onPhotoClick)
            message.isVoice -> VoiceMessage(message, contentColor)
            else -> Text(
                text = message.message,
                color = contentColor
            )
        }
    }
}

@Composable
private fun ReplyQuote(
    sender: String,
    text: String,
    contentColor: Color
) {
    Row(
        modifier = Modifier
            .widthIn(max = 230.dp)
            .background(
                color = contentColor.copy(alpha = 0.14f),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(width = 3.dp, height = 34.dp)
                .background(contentColor.copy(alpha = 0.65f), RoundedCornerShape(12.dp))
        )
        Column {
            Text(
                text = sender.ifBlank { "Сообщение" },
                color = contentColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = text,
                color = contentColor.copy(alpha = 0.75f),
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun MessagesDate(
    date: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (date != System.currentTimeMillis().toDateString()) date else "Сегодня",
            fontWeight = FontWeight.Medium,
            color = DanTalkTheme.colors.oppositeTheme
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
private fun MessageTail(
    isCurrentUserMessage: Boolean = false,
) {
    val tailColor =
        if (isCurrentUserMessage) DanTalkTheme.colors.main else DanTalkTheme.colors.singleTheme

    Canvas(
        modifier = Modifier
            .size(width = 14.dp, height = 12.dp)
            .offset(
                x = if (isCurrentUserMessage) 2.dp else (-2).dp,
                y = 0.dp
            )
    ) {
        val path = Path().apply {
            if (isCurrentUserMessage) {
                moveTo(0f, 0f)
                cubicTo(
                    x1 = 0f,
                    y1 = 0f,
                    x2 = size.width * 0.1f,
                    y2 = size.height * 0.9f,
                    x3 = 0f,
                    y3 = size.height
                )
                lineTo(size.width * 0.8f, size.height)
            } else {
                moveTo(size.width, 0f)
                cubicTo(
                    x1 = size.width,
                    y1 = 0f,
                    x2 = 0f,
                    y2 = size.height,
                    x3 = size.width * 0.2f,
                    y3 = size.height
                )
                lineTo(size.width, size.height)
            }
            close()
        }
        drawPath(path, color = tailColor)
    }
}

@Composable
private fun MessagePhoto(
    url: String,
    contentColor: Color,
    onClick: () -> Unit
) {
    var aspectRatio by remember { mutableFloatStateOf(1f) }

    val context = LocalContext.current
    val request = ImageRequest.Builder(context)
        .data(url)
        .size(Size.ORIGINAL)
        .listener(
            onSuccess = { _, result ->
                val width = result.image.width
                val height = result.image.height
                if (width > 0 && height > 0) {
                    aspectRatio = width.toFloat() / height.toFloat()
                }
            }
        )
        .build()

    val painter = rememberAsyncImagePainter(request)
    val state by painter.state.collectAsState()

    Box(
        modifier = Modifier
            .widthIn(max = 240.dp)
            .aspectRatio(aspectRatio)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        when (state) {
            is AsyncImagePainter.State.Loading -> {
                CircularProgressIndicator(
                    color = contentColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            is AsyncImagePainter.State.Error -> {
                Text(
                    text = "Не удалось загрузить фото",
                    color = contentColor
                )
            }

            is AsyncImagePainter.State.Success -> {
                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }

            is AsyncImagePainter.State.Empty -> {
                Log.d("MessagePhoto", "Empty")
            }
        }
    }
}

@Composable
private fun VoiceMessage(
    message: UiMessage,
    contentColor: Color
) {
    var isPrepared by remember(message.message) { mutableStateOf(false) }
    var isPlaying by remember(message.message) { mutableStateOf(false) }
    var durationMillis by remember(message.message) {
        mutableIntStateOf(message.mediaDurationMillis.toInt())
    }

    val player = remember(message.message) {
        runCatching {
            MediaPlayer().apply {
                setDataSource(message.message)
                setOnPreparedListener {
                    isPrepared = true
                    if (durationMillis <= 0) durationMillis = it.duration
                }
                setOnCompletionListener {
                    isPlaying = false
                    runCatching { seekTo(0) }
                }
                prepareAsync()
            }
        }.getOrNull()
    }

    DisposableEffect(player) {
        onDispose {
            player?.release()
        }
    }

    Row(
        modifier = Modifier.widthIn(min = 190.dp, max = 240.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconButton(
            enabled = isPrepared && player != null,
            onClick = {
                val currentPlayer = player ?: return@IconButton
                if (isPlaying) {
                    currentPlayer.pause()
                    isPlaying = false
                } else {
                    currentPlayer.start()
                    isPlaying = true
                }
            },
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = contentColor,
                disabledContentColor = contentColor.copy(alpha = 0.45f)
            )
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = null
            )
        }
        Icon(
            imageVector = Icons.Outlined.Mic,
            contentDescription = null,
            tint = contentColor.copy(alpha = 0.8f),
            modifier = Modifier.size(18.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Голосовое сообщение",
                color = contentColor,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = formatDurationMillis(durationMillis),
                color = contentColor.copy(alpha = 0.75f),
                fontSize = 12.sp
            )
        }
    }
}

private fun formatDurationMillis(durationMillis: Int): String {
    val safeDuration = durationMillis.coerceAtLeast(0)
    val totalSeconds = safeDuration / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}

private val UiMessage.isMedia: Boolean
    get() = isPhoto || isVoice
