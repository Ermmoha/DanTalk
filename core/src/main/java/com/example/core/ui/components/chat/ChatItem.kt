package com.example.core.ui.components.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.core.design.theme.DanTalkTheme
import com.example.core.ui.model.UiChat
import com.example.core.util.toDateString
import kotlin.math.max

@Composable
fun ChatItem(
    modifier: Modifier = Modifier,
    chat: UiChat,
    onChatClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .background(DanTalkTheme.colors.singleTheme)
            .clickable { onChatClick() },
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Spacer(Modifier.height(0.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AsyncImage(
                model = chat.user.avatar,
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = chat.user.username,
                            fontSize = 16.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.Medium,
                            color = DanTalkTheme.colors.oppositeTheme
                        )
                        if (chat.isPinned) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = DanTalkTheme.colors.main
                            )
                        }
                    }
                    Text(
                        text = if (chat.lastMessage?.date != System.currentTimeMillis()
                                .toDateString()
                        )
                            chat.lastMessage?.date ?: ""
                        else chat.lastMessage.time,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = DanTalkTheme.colors.hint
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    chat.lastMessage.let { lastMsg ->
                        if (lastMsg?.isPhoto == true)
                            LastPhotoMessage(
                                url = lastMsg.message,
                                modifier = Modifier.weight(1f)
                            )
                        else
                            Text(
                                text = if (lastMsg?.isCurrentUserMessage == true)
                                    "Вы: ${lastMsg.message}"
                                else lastMsg?.message ?: "Нет сообщений",
                                modifier = Modifier.weight(1f),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                color = DanTalkTheme.colors.hint
                            )
                        chat.unreadMessagesCount.let {
                            if (it > 0) NewMessagesIndicator(it)
                        }
                        if (chat.unreadMessagesCount < 1 && lastMsg != null && lastMsg.isCurrentUserMessage)
                            MessageStatus(
                                isRead = lastMsg.read,
                                isPending = lastMsg.isPending
                            )
                    }
                }
            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 10.dp),
            color = DanTalkTheme.colors.spacer
        )
    }
}

@Composable
private fun NewMessagesIndicator(
    amount: Int,
) {
    val color = DanTalkTheme.colors.main
    Text(
        text = if(amount < 1000) amount.toString() else "999+",
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .drawBehind {
                val padding = 8.dp.toPx()
                val height = this.size.height + padding
                val width = this.size.width + padding

                if (amount < 10) {
                    val diameter = max(width, height)
                    drawCircle(
                        color = color,
                        radius = diameter / 2,
                        center = center
                    )
                } else
                    drawRoundRect(
                        color = color,
                        size = Size(width, height),
                        topLeft = Offset(
                            (size.width - width) / 2,
                            (size.height - height) / 2
                        ),
                        cornerRadius = CornerRadius(height)
                    )
            }
            .padding(horizontal = 6.dp, vertical = 2.dp),
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        color = Color.White
    )
}

@Composable
private fun MessageStatus(
    isRead: Boolean,
    isPending: Boolean,
) {
    Icon(
        imageVector = if (isPending) Icons.Default.Pending else Icons.Default.Check,
        contentDescription = null,
        modifier = Modifier.size(18.dp),
        tint = if (!isPending && isRead) DanTalkTheme.colors.main else DanTalkTheme.colors.hint
    )
}

@Composable
private fun LastPhotoMessage(
    url: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        AsyncImage(
            model = url,
            contentDescription = null,
            modifier = Modifier
                .size(20.dp)
                .clip(RoundedCornerShape(4.dp)),
            contentScale = ContentScale.Crop
        )
        Text(
            text = "Изображение",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            color = DanTalkTheme.colors.main
        )
    }
}
