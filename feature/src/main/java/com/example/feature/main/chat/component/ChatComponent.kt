package com.example.feature.main.chat.component

import com.example.feature.main.chat.store.ChatStore
import kotlinx.coroutines.flow.StateFlow

interface ChatComponent {
    val state: StateFlow<ChatStore.State>

    fun onIntent(intent: ChatStore.Intent)
}