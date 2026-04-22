package com.example.dantalk.features.main.profile.component

import com.example.feature.main.profile.store.ProfileStore
import kotlinx.coroutines.flow.StateFlow

interface ProfileComponent {
    val state: StateFlow<ProfileStore.State>

    fun onIntent(intent: ProfileStore.Intent)
}