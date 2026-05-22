package com.example.feature.main.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.Vibration
import androidx.compose.material.icons.outlined.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.design.AppTheme
import com.example.core.design.theme.DanTalkTheme
import com.example.core.ui.components.AvatarImage
import com.example.feature.main.settings.component.SettingsComponent
import com.example.feature.main.settings.store.SettingsStore
import com.example.feature.main.settings.ui.components.SettingsTopBar

@Composable
fun SettingsContent(
    component: SettingsComponent
) {
    val state by component.state.collectAsState()

    Scaffold(
        topBar = {
            SettingsTopBar(
                title = "Настройки",
                navigateBack = { component.onIntent(SettingsStore.Intent.NavigateBack) }
            )
        },
        containerColor = DanTalkTheme.colors.altSingleTheme
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item { Header(state = state) }
            item {
                ThemeCard(
                    selectedTheme = state.selectedTheme,
                    onThemeChange = {
                        component.onIntent(SettingsStore.Intent.ChangeTheme(it))
                    }
                )
            }
            item {
                NotificationCard(
                    enabled = state.notificationsEnabled,
                    previewsEnabled = state.notificationPreviewsEnabled,
                    soundEnabled = state.notificationSoundEnabled,
                    vibrationEnabled = state.notificationVibrationEnabled,
                    onEnabledChange = {
                        component.onIntent(SettingsStore.Intent.SetNotificationsEnabled(it))
                    },
                    onPreviewsEnabledChange = {
                        component.onIntent(
                            SettingsStore.Intent.SetNotificationPreviewsEnabled(it)
                        )
                    },
                    onSoundEnabledChange = {
                        component.onIntent(SettingsStore.Intent.SetNotificationSoundEnabled(it))
                    },
                    onVibrationEnabledChange = {
                        component.onIntent(
                            SettingsStore.Intent.SetNotificationVibrationEnabled(it)
                        )
                    }
                )
            }
            item {
                ActionCard(
                    icon = Icons.Outlined.Info,
                    title = "Помощь",
                    description = "Открыть справку по приложению",
                    onClick = { component.onIntent(SettingsStore.Intent.NavigateToHelp) }
                )
            }
        }
    }
}

@Composable
private fun Header(
    state: SettingsStore.State
) {
    SettingsCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AvatarImage(
                model = state.user.avatar,
                name = state.user.username,
                modifier = Modifier
                    .size(54.dp)
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = state.user.username.ifBlank { "User" },
                    color = DanTalkTheme.colors.oppositeTheme,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                Text(
                    text = state.user.email,
                    color = DanTalkTheme.colors.hint,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun ThemeCard(
    selectedTheme: AppTheme,
    onThemeChange: (AppTheme) -> Unit
) {
    val items = listOf(
        AppTheme.SYSTEM to "Системная",
        AppTheme.LIGHT to "Светлая",
        AppTheme.DARK to "Темная"
    )

    SettingsCard {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SectionTitle(
                icon = Icons.Outlined.Palette,
                title = "Тема"
            )
            HorizontalDivider(color = DanTalkTheme.colors.spacer)
            items.forEach { (theme, title) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onThemeChange(theme) }
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        color = DanTalkTheme.colors.oppositeTheme
                    )
                    RadioButton(
                        selected = selectedTheme == theme,
                        onClick = { onThemeChange(theme) },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = DanTalkTheme.colors.main,
                            unselectedColor = DanTalkTheme.colors.hint
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationCard(
    enabled: Boolean,
    previewsEnabled: Boolean,
    soundEnabled: Boolean,
    vibrationEnabled: Boolean,
    onEnabledChange: (Boolean) -> Unit,
    onPreviewsEnabledChange: (Boolean) -> Unit,
    onSoundEnabledChange: (Boolean) -> Unit,
    onVibrationEnabledChange: (Boolean) -> Unit
) {
    SettingsCard {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SettingSwitchRow(
                icon = Icons.Outlined.Notifications,
                title = "Уведомления о сообщениях",
                description = "Получать уведомления о новых сообщениях",
                checked = enabled,
                onCheckedChange = onEnabledChange
            )
            HorizontalDivider(color = DanTalkTheme.colors.spacer)
            SettingSwitchRow(
                icon = Icons.Outlined.Visibility,
                title = "Содержимое сообщений",
                description = "Показывать текст сообщений в уведомлениях",
                checked = previewsEnabled,
                enabled = enabled,
                onCheckedChange = onPreviewsEnabledChange
            )
            SettingSwitchRow(
                icon = Icons.Outlined.VolumeUp,
                title = "Звук уведомлений",
                description = "Включить звук при получении уведомлений",
                checked = soundEnabled,
                enabled = enabled,
                onCheckedChange = onSoundEnabledChange
            )
            SettingSwitchRow(
                icon = Icons.Outlined.Vibration,
                title = "Вибрация",
                description = "Включить вибрацию при получении уведомлений",
                checked = vibrationEnabled,
                enabled = enabled,
                onCheckedChange = onVibrationEnabledChange
            )
        }
    }
}

@Composable
private fun SettingSwitchRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) { onCheckedChange(!checked) },
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (enabled) DanTalkTheme.colors.main else DanTalkTheme.colors.hint
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                color = DanTalkTheme.colors.oppositeTheme,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = description,
                color = DanTalkTheme.colors.hint,
                fontSize = 12.sp
            )
        }
        Switch(
            checked = checked,
            enabled = enabled,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = DanTalkTheme.colors.main,
                uncheckedThumbColor = DanTalkTheme.colors.hint,
                uncheckedTrackColor = DanTalkTheme.colors.altSingleTheme,
                disabledCheckedThumbColor = Color.White.copy(alpha = 0.7f),
                disabledCheckedTrackColor = DanTalkTheme.colors.main.copy(alpha = 0.35f),
                disabledUncheckedThumbColor = DanTalkTheme.colors.hint.copy(alpha = 0.6f),
                disabledUncheckedTrackColor = DanTalkTheme.colors.altSingleTheme.copy(alpha = 0.7f)
            )
        )
    }
}

@Composable
private fun ActionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    SettingsCard(
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = DanTalkTheme.colors.main
            )
            Column {
                Text(
                    text = title,
                    color = DanTalkTheme.colors.oppositeTheme,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = description,
                    color = DanTalkTheme.colors.hint,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = DanTalkTheme.colors.main
        )
        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = DanTalkTheme.colors.oppositeTheme
        )
    }
}

@Composable
private fun SettingsCard(
    modifier: Modifier = Modifier,
    containerColor: Color = DanTalkTheme.colors.singleTheme,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        )
    ) {
        content()
    }
}
