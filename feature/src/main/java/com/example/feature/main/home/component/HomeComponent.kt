package com.example.feature.main.home.component

import com.example.feature.main.home.store.HomeStore
import kotlinx.coroutines.flow.StateFlow

interface HomeComponent {
    val state: StateFlow<HomeStore.State>

    fun onIntent(intent: HomeStore.Intent)
}