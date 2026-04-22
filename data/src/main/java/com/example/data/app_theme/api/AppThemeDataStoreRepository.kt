package com.example.data.app_theme.api

import com.example.core.design.AppTheme
import kotlinx.coroutines.flow.Flow

interface AppThemeDataStoreRepository {
    val themeFlow: Flow<AppTheme>

    suspend fun setTheme(theme: AppTheme)
}