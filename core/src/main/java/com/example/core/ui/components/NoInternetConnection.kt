package com.example.core.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
//    var expanded by remember { mutableStateOf(false) }
//    val isUnavailable = connectionState is ConnectionState.Unavailable
//
//    val tint by animateColorAsState(
//        targetValue = if (isUnavailable) DanTalkTheme.colors.red else DanTalkTheme.colors.main,
//        animationSpec = tween(250),
//        label = "network_state_color"
//    )
//
//    val title = if (isUnavailable) "No internet connection" else "Limited connection"
//    val subtitle = if (isUnavailable) {
//        "Check Wi-Fi or mobile network"
//    } else {
//        "Connected without internet validation"
//    }
//
//    Column(
//        modifier = modifier
//            .padding(8.dp)
//            .background(
//                color = DanTalkTheme.colors.altSingleTheme,
//                shape = RoundedCornerShape(16.dp)
//            )
//            .clickable { expanded = !expanded }
//            .padding(horizontal = 12.dp, vertical = 10.dp),
//        verticalArrangement = Arrangement.spacedBy(6.dp)
//    ) {
//        Row(
//            horizontalArrangement = Arrangement.spacedBy(8.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Icon(
//                imageVector = if (isUnavailable) {
//                    Icons.Default.WifiOff
//                } else {
//                    Icons.Default.Wifi
//                },
//                contentDescription = null,
//                tint = tint,
//                modifier = Modifier.size(18.dp)
//            )
//            Text(
//                text = title,
//                color = tint,
//                fontSize = 13.sp
//            )
//        }
//        AnimatedVisibility(
//            visible = expanded,
//            enter = fadeIn(tween(180)) + expandVertically(tween(180)),
//            exit = fadeOut(tween(120)) + shrinkVertically(tween(120))
//        ) {
//            Text(
//                text = subtitle,
//                color = DanTalkTheme.colors.hint,
//                fontSize = 12.sp
//            )
//        }
//    }
}
