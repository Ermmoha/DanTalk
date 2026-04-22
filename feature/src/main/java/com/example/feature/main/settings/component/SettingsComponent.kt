package com.example.feature.main.settings.component

import com.example.feature.main.settings.store.SettingsStore
import kotlinx.coroutines.flow.StateFlow

interface SettingsComponent {
    val state: StateFlow<SettingsStore.State>

    fun onIntent(intent: SettingsStore.Intent)
}
