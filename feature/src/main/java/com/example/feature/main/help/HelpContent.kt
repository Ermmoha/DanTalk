package com.example.feature.main.help

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.design.theme.DanTalkTheme
import com.example.feature.main.help.component.HelpComponent
import com.example.feature.main.settings.ui.components.SettingsTopBar

private data class HelpItem(
    val icon: ImageVector,
    val title: String,
    val description: String
)

@Composable
fun HelpContent(
    component: HelpComponent
) {
    val items = listOf(
        HelpItem(
            icon = Icons.Filled.ChatBubble,
            title = "Messages and chats",
            description = "Swipe a chat to pin or delete it. Long press your message to edit or delete."
        ),
        HelpItem(
            icon = Icons.Outlined.Notifications,
            title = "Notifications",
            description = "Enable notifications in Settings to receive alerts for incoming messages."
        ),
        HelpItem(
            icon = Icons.Outlined.Settings,
            title = "Connection status",
            description = "The floating network widget shows unavailable and limited internet states."
        ),
        HelpItem(
            icon = Icons.Outlined.Info,
            title = "Privacy",
            description = "Messages are stored in your account data. Sign out from the drawer when needed."
        )
    )

    Scaffold(
        topBar = { SettingsTopBar(title = "Help", navigateBack = component::navigateBack) },
        containerColor = DanTalkTheme.colors.singleTheme
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(items) { item ->
                HelpCard(item)
            }
        }
    }
}

@Composable
private fun HelpCard(
    item: HelpItem
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = DanTalkTheme.colors.altSingleTheme
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = DanTalkTheme.colors.main
            )
            Text(
                text = item.title,
                color = DanTalkTheme.colors.oppositeTheme,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
            Text(
                text = item.description,
                color = DanTalkTheme.colors.hint,
                fontSize = 13.sp
            )
        }
    }
}
