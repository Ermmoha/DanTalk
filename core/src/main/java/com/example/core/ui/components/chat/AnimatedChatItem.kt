package com.example.core.ui.components.chat

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.core.design.theme.DanTalkTheme
import com.example.core.ui.model.UiChat
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private enum class SwipeAnchors {
    Center, Right
}

@Composable
fun AnimatedChatItem(
    chat: UiChat,
    onChatClick: () -> Unit,
    onPinIconClick: () -> Unit,
    onDeleteIconClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    val swipeState = remember {
        AnchoredDraggableState(
            initialValue = SwipeAnchors.Center,
            positionalThreshold = { distance -> distance * 0.5f },
            velocityThreshold = { 100f },
            snapAnimationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessLow
            ),
            decayAnimationSpec = exponentialDecay()
        ).apply {
            updateAnchors(
                DraggableAnchors {
                    SwipeAnchors.Center at 0f
                    SwipeAnchors.Right at -280f
                }
            )
        }
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterEnd
    ) {
        AnimatedChatItemAction(
            isPinned = chat.isPinned,
            onPinIconClick = {
                coroutineScope.launch {
                    onPinIconClick()
                    swipeState.animateTo(SwipeAnchors.Center)
                }
            },
            onDeleteIconClick = {
                coroutineScope.launch {
                    onDeleteIconClick()
                    swipeState.animateTo(SwipeAnchors.Center)
                }
            }
        )
        ChatItem(
            modifier = Modifier
                .fillMaxWidth()
                .offset {
                    IntOffset(
                        x = swipeState.offset.roundToInt(),
                        y = 0
                    )
                }
                .anchoredDraggable(
                    state = swipeState,
                    orientation = Orientation.Horizontal
                ),
            chat = chat,
            onChatClick = onChatClick
        )
    }
}

@Composable
private fun AnimatedChatItemAction(
    isPinned: Boolean,
    onPinIconClick: () -> Unit,
    onDeleteIconClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .background(Color.Transparent)
            .padding(end = 10.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Row {
            IconButton(
                onClick = onPinIconClick,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = DanTalkTheme.colors.hint
                )
            ) {
                Icon(
                    imageVector = if (isPinned) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
            IconButton(
                onClick = onDeleteIconClick,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = DanTalkTheme.colors.hint
                )
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = null
                )
            }
        }
    }
}
