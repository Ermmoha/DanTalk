package com.example.data.user.impl

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.data.user.api.UserDataStoreRepository
import com.example.data.user.api.model.UserData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class UserDataStoreRepositoryImpl(private val context: Context) : UserDataStoreRepository {
    companion object {
        private val Context.dataStore by preferencesDataStore("user_data")

        private val USER_ID = stringPreferencesKey("user_id")
        private val USER_AVATAR = stringPreferencesKey("user_avatar")
        private val USER_EMAIL = stringPreferencesKey("user_email")
        private val USER_USERNAME = stringPreferencesKey("user_username")
        private val USER_FIRSTNAME = stringPreferencesKey("user_firstname")
        private val USER_LASTNAME = stringPreferencesKey("user_lastname")
        private val USER_PATRONYMIC = stringPreferencesKey("user_patronymic")
    }

    override val getUserData: Flow<UserData> = context.dataStore.data.map { preferences ->
        UserData(
            id = preferences[USER_ID] ?: "",
            avatar = preferences[USER_AVATAR] ?: "",
            email = preferences[USER_EMAIL] ?: "",
            username = preferences[USER_USERNAME] ?: "",
            firstname = preferences[USER_FIRSTNAME] ?: "",
            lastname = preferences[USER_LASTNAME] ?: "",
            patronymic = preferences[USER_PATRONYMIC] ?: ""
        )
    }

    override suspend fun saveUserData(userData: UserData) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID] = userData.id
            preferences[USER_AVATAR] = userData.avatar
            preferences[USER_EMAIL] = userData.email
            preferences[USER_USERNAME] = userData.username
            preferences[USER_FIRSTNAME] = userData.firstname
            preferences[USER_LASTNAME] = userData.lastname
            preferences[USER_PATRONYMIC] = userData.patronymic
        }
    }

    override suspend fun clearUserData() {
        context.dataStore.edit { preferences ->
            preferences[USER_ID] = ""
            preferences[USER_AVATAR] = ""
            preferences[USER_EMAIL] = ""
            preferences[USER_USERNAME] = ""
            preferences[USER_FIRSTNAME] = ""
            preferences[USER_LASTNAME] = ""
            preferences[USER_PATRONYMIC] = ""
        }
    }
}