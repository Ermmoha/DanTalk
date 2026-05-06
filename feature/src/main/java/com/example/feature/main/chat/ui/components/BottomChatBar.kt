package com.example.feature.main.chat.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.design.theme.DanTalkTheme
import com.example.core.ui.model.UiMessage

@Composable
fun BottomChatBar(
    message: String,
    isEditing: Boolean,
    isRecording: Boolean,
    recordingSeconds: Int,
    replyingToMessage: UiMessage?,
    onMessageChange: (String) -> Unit,
    cancelEdit: () -> Unit,
    cancelReply: () -> Unit,
    startVoiceRecording: () -> Unit,
    sendVoiceRecording: () -> Unit,
    cancelVoiceRecording: () -> Unit,
    sendMessage: () -> Unit,
    sendPhoto: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .background(DanTalkTheme.colors.singleTheme)
            .imePadding()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        AnimatedVisibility(visible = isEditing && !isRecording) {
            EditingHeader(
                text = message,
                onCancel = cancelEdit
            )
        }
        AnimatedVisibility(visible = replyingToMessage != null && !isRecording && !isEditing) {
            replyingToMessage?.let {
                ReplyHeader(
                    message = it,
                    onCancel = cancelReply
                )
            }
        }

        if (isRecording) {
            RecordingPanel(
                recordingSeconds = recordingSeconds,
                onCancel = cancelVoiceRecording,
                onSend = sendVoiceRecording
            )
        } else {
            MessageInput(
                message = message,
                isEditing = isEditing,
                onMessageChange = onMessageChange,
                sendMessage = sendMessage,
                sendPhoto = sendPhoto,
                startVoiceRecording = startVoiceRecording
            )
        }
    }
}

@Composable
private fun ReplyHeader(
    message: UiMessage,
    onCancel: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = DanTalkTheme.colors.main.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(start = 10.dp, end = 2.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(width = 3.dp, height = 34.dp)
                .background(DanTalkTheme.colors.main, RoundedCornerShape(12.dp))
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (message.isCurrentUserMessage) "Вы" else "Ответ на сообщение",
                color = DanTalkTheme.colors.main,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = message.previewText(),
                color = DanTalkTheme.colors.hint,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        IconButton(onClick = onCancel) {
            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = DanTalkTheme.colors.hint
            )
        }
    }
}

@Composable
private fun EditingHeader(
    text: String,
    onCancel: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = DanTalkTheme.colors.main.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(start = 10.dp, end = 2.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.Edit,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = DanTalkTheme.colors.main
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Редактирование",
                color = DanTalkTheme.colors.main,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = text,
                color = DanTalkTheme.colors.hint,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        IconButton(onClick = onCancel) {
            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = DanTalkTheme.colors.hint
            )
        }
    }
}

@Composable
private fun RecordingPanel(
    recordingSeconds: Int,
    onCancel: () -> Unit,
    onSend: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = DanTalkTheme.colors.altSingleTheme,
                shape = RoundedCornerShape(18.dp)
            )
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconButton(
            onClick = onCancel,
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = DanTalkTheme.colors.red
            )
        ) {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = null
            )
        }
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Mic,
                contentDescription = null,
                tint = DanTalkTheme.colors.red,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = formatDuration(recordingSeconds),
                color = DanTalkTheme.colors.oppositeTheme,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Запись голосового",
                color = DanTalkTheme.colors.hint,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        IconButton(
            onClick = onSend,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = DanTalkTheme.colors.main,
                contentColor = Color.White
            )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = null
            )
        }
    }
}

@Composable
private fun MessageInput(
    message: String,
    isEditing: Boolean,
    onMessageChange: (String) -> Unit,
    sendMessage: () -> Unit,
    sendPhoto: () -> Unit,
    startVoiceRecording: () -> Unit
) {
    BasicTextField(
        value = message,
        onValueChange = onMessageChange,
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = DanTalkTheme.colors.altSingleTheme,
                shape = RoundedCornerShape(18.dp)
            )
            .padding(start = 12.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
        textStyle = LocalTextStyle.current.copy(
            color = DanTalkTheme.colors.oppositeTheme,
            fontSize = 16.sp
        ),
        maxLines = 4,
        cursorBrush = SolidColor(DanTalkTheme.colors.main),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (message.isEmpty()) {
                        Text(
                            text = "Сообщение",
                            fontSize = 16.sp,
                            color = DanTalkTheme.colors.hint
                        )
                    }
                    innerTextField()
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AnimatedVisibility(
                        visible = message.isBlank() && !isEditing,
                        enter = fadeIn(tween(250)),
                        exit = fadeOut(tween(120))
                    ) {
                        Row {
                            IconButton(
                                onClick = sendPhoto,
                                colors = IconButtonDefaults.iconButtonColors(
                                    contentColor = DanTalkTheme.colors.hint
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Image,
                                    contentDescription = null
                                )
                            }
                            IconButton(
                                onClick = startVoiceRecording,
                                colors = IconButtonDefaults.iconButtonColors(
                                    contentColor = DanTalkTheme.colors.main
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Mic,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                    AnimatedVisibility(visible = message.isNotBlank()) {
                        IconButton(
                            onClick = sendMessage,
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = DanTalkTheme.colors.main
                            )
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    )
}

private fun formatDuration(seconds: Int): String {
    val minutes = seconds / 60
    val rest = seconds % 60
    return "%d:%02d".format(minutes, rest)
}

private fun UiMessage.previewText(): String =
    when {
        isPhoto -> "Фото"
        isVoice -> "Голосовое сообщение"
        else -> message
    }
