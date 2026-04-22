package com.example.data.settings.impl

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.data.settings.api.AppSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class AppSettingsRepositoryImpl(
    private val context: Context
) : AppSettingsRepository {

    companion object {
        private val Context.dataStore by preferencesDataStore("app_settings")

        private val NOTIFICATIONS_ENABLED_KEY = booleanPreferencesKey("notifications_enabled")
        private val PINNED_CHATS_KEY = stringSetPreferencesKey("pinned_chats")
    }

    override val notificationsEnabledFlow: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            preferences[NOTIFICATIONS_ENABLED_KEY] ?: true
        }

    override val pinnedChatsFlow: Flow<Set<String>> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            preferences[PINNED_CHATS_KEY] ?: emptySet()
        }

    override suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATIONS_ENABLED_KEY] = enabled
        }
    }

    override suspend fun setChatPinned(chatId: String, pinned: Boolean) {
        context.dataStore.edit { preferences ->
            val current = preferences[PINNED_CHATS_KEY]?.toMutableSet() ?: mutableSetOf()
            if (pinned) current.add(chatId) else current.remove(chatId)
            preferences[PINNED_CHATS_KEY] = current
        }
    }

    override suspend fun togglePinnedChat(chatId: String) {
        context.dataStore.edit { preferences ->
            val current = preferences[PINNED_CHATS_KEY]?.toMutableSet() ?: mutableSetOf()
            if (!current.add(chatId)) current.remove(chatId)
            preferences[PINNED_CHATS_KEY] = current
        }
    }
}
