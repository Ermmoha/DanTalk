package com.example.feature.main.settings.store

import com.arkivanov.mvikotlin.core.store.Store
import com.example.core.design.AppTheme
import com.example.core.ui.model.UiUserData
import com.example.feature.main.settings.store.SettingsStore.Intent
import com.example.feature.main.settings.store.SettingsStore.Label
import com.example.feature.main.settings.store.SettingsStore.State

interface SettingsStore : Store<Intent, State, Label> {

    sealed interface Intent {
        class ChangeTheme(val theme: AppTheme) : Intent
        class SetNotificationsEnabled(val enabled: Boolean) : Intent
        class SetNotificationPreviewsEnabled(val enabled: Boolean) : Intent
        class SetNotificationSoundEnabled(val enabled: Boolean) : Intent
        class SetNotificationVibrationEnabled(val enabled: Boolean) : Intent
        data object NavigateToHelp : Intent
        data object NavigateBack : Intent
    }

    data class State(
        val user: UiUserData = UiUserData(),
        val selectedTheme: AppTheme = AppTheme.SYSTEM,
        val notificationsEnabled: Boolean = true,
        val notificationPreviewsEnabled: Boolean = true,
        val notificationSoundEnabled: Boolean = true,
        val notificationVibrationEnabled: Boolean = true
    )

    sealed interface Label {
        data object NavigateToHelp : Label
        data object NavigateBack : Label
    }
}
