package com.example.feature.main.chat.ui.components

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.size.Size
import com.example.core.design.theme.DanTalkTheme
import com.example.core.ui.model.UiMessage
import com.example.core.util.toDateString

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun Message(
    message: UiMessage,
    onEditClick: (UiMessage) -> Unit,
    onDeleteClick: (UiMessage) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

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
                            onClick = {},
                            onLongClick = {
                                if (message.isCurrentUserMessage) expanded = true
                            }
                        )
                        .background(
                            color = if (message.isCurrentUserMessage) DanTalkTheme.colors.main else DanTalkTheme.colors.singleTheme,
                            shape = messageShape
                        )
                        .padding(10.dp)
                ) {
                    if (!message.isPhoto)
                        Text(
                            text = message.message,
                            color = if (message.isCurrentUserMessage)
                                Color.White
                            else
                                DanTalkTheme.colors.oppositeTheme
                        )
                    else
                        MessagePhoto(message.message)

                }
                MessageTail(
                    isCurrentUserMessage = message.isCurrentUserMessage
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    if (!message.isPhoto) {
                        DropdownMenuItem(
                            text = { Text(text = "Изменить") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Edit,
                                    contentDescription = null
                                )
                            },
                            onClick = {
                                expanded = false
                                onEditClick(message)
                            }
                        )
                    }
                    DropdownMenuItem(
                        text = { Text(text = "Удалить") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = null
                            )
                        },
                        onClick = {
                            expanded = false
                            onDeleteClick(message)
                        }
                    )
                }
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
                    modifier = Modifier
                        .padding(top = 3.dp),
                    color = DanTalkTheme.colors.hint,
                    fontSize = 10.sp
                )
                if (message.isCurrentUserMessage)
                    Icon(
                        imageVector = if(!message.isPending) Icons.Default.Check else Icons.Default.Pending,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = if (message.read) DanTalkTheme.colors.main else DanTalkTheme.colors.hint
                    )
            }
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
            text = if (date != System.currentTimeMillis().toDateString())
                date else "Сегодня",
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
    url: String
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
                if (width > 0 && height > 0)
                    aspectRatio = width.toFloat() / height.toFloat()
            }
        )
        .build()

    val painter = rememberAsyncImagePainter(request)
    val state by painter.state.collectAsState()

    Box(
        modifier = Modifier
            .widthIn(max = 240.dp)
            .aspectRatio(aspectRatio),
        contentAlignment = Alignment.Center
    ) {
        when (state) {
            is AsyncImagePainter.State.Loading -> {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            is AsyncImagePainter.State.Error -> {
                Text(
                    text = "Не удалось загрузить фото",
                    color = Color.White
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
