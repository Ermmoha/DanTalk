package com.example.data.app_theme.impl

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.core.design.AppTheme
import com.example.data.app_theme.api.AppThemeDataStoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppThemeDataStoreRepositoryImpl(private val context: Context) : AppThemeDataStoreRepository {
    companion object {
        val Context.dataStore by preferencesDataStore("app_theme")

        private val THEME_KEY = stringPreferencesKey("theme")
    }

    override val themeFlow: Flow<AppTheme> = context.dataStore.data.map { preferences ->
        when (preferences[THEME_KEY]) {
            AppTheme.LIGHT.name -> AppTheme.LIGHT
            AppTheme.DARK.name -> AppTheme.DARK
            else -> AppTheme.SYSTEM
        }
    }

    override suspend fun setTheme(theme: AppTheme) {
        context.dataStore.edit { it[THEME_KEY] = theme.name }
    }
}