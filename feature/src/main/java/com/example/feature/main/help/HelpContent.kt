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
            title = "Сообщения и чаты",
            description = "Свайпните чат влево, чтобы закрепить или удалить. Зажмите на сообщение, чтобы удалить или изменить его."
        ),
        HelpItem(
            icon = Icons.Outlined.Notifications,
            title = "Уведомления",
            description = "Включите уведомления в настройках, чтобы знать о входящих сообщениях."
        ),
        HelpItem(
            icon = Icons.Outlined.Settings,
            title = "Интернет-соединение",
            description = "Для отправки сообщений необходимо стабильное интернет-соединение, но когда его нет, отправленные сообщения сохраняются, и при подключении к интернету будут отправлены."
        ),
        HelpItem(
            icon = Icons.Outlined.Info,
            title = "Спасибо",
            description = "Спасибо, что пользуетесь мессенджером DanTalk."
        )
    )

    Scaffold(
        topBar = { SettingsTopBar(title = "Справка", navigateBack = component::navigateBack) },
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
