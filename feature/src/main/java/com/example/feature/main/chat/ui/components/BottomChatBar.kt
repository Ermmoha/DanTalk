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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.design.theme.DanTalkTheme

@Composable
fun BottomChatBar(
    message: String,
    isEditing: Boolean,
    onMessageChange: (String) -> Unit,
    cancelEdit: () -> Unit,
    sendMessage: () -> Unit,
    sendPhoto: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .background(DanTalkTheme.colors.singleTheme)
            .imePadding()
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        AnimatedVisibility(visible = isEditing) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Редактирование сообщения",
                    fontSize = 12.sp,
                    color = DanTalkTheme.colors.main
                )
                IconButton(
                    onClick = cancelEdit,
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = DanTalkTheme.colors.hint
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        BasicTextField(
            value = message,
            onValueChange = onMessageChange,
            modifier = Modifier.fillMaxWidth(),
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
                    Row {
                        AnimatedVisibility(
                            visible = message.isBlank() && !isEditing,
                            enter = fadeIn(tween(400)),
                            exit = fadeOut(tween(160))
                        ) {
                            IconButton(
                                onClick = sendPhoto,
                                colors = IconButtonDefaults.iconButtonColors(
                                    contentColor = DanTalkTheme.colors.hint
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null
                                )
                            }
                        }
                        AnimatedVisibility(
                            visible = message.isNotBlank()
                        ) {
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
}
