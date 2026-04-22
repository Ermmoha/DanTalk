package com.example.data.settings.api

import kotlinx.coroutines.flow.Flow

interface AppSettingsRepository {
    val notificationsEnabledFlow: Flow<Boolean>
    val pinnedChatsFlow: Flow<Set<String>>

    suspend fun setNotificationsEnabled(enabled: Boolean)
    suspend fun setChatPinned(chatId: String, pinned: Boolean)
    suspend fun togglePinnedChat(chatId: String)
}
