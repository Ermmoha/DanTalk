package com.example.core.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.design.theme.DanTalkTheme
import com.example.core.network_observer.ConnectionState

@Composable
fun NoInternetConnection(
    connectionState: ConnectionState,
    modifier: Modifier = Modifier,
) {
    val isUnavailable = connectionState is ConnectionState.Unavailable
    val tint by animateColorAsState(
        targetValue = DanTalkTheme.colors.red,
        animationSpec = tween(250),
        label = "network_state_color"
    )

    AnimatedVisibility(
        visible = isUnavailable,
        enter = fadeIn(tween(180)) + expandVertically(tween(180)),
        exit = fadeOut(tween(120)) + shrinkVertically(tween(120)),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .background(
                    color = DanTalkTheme.colors.singleTheme.copy(alpha = 0.96f),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.WifiOff,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = "Нет интернет-соединения",
                color = tint,
                fontSize = 13.sp
            )
        }
    }
}
