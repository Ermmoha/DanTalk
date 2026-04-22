package com.example.data.di

import com.example.data.app_theme.api.AppThemeDataStoreRepository
import com.example.data.app_theme.impl.AppThemeDataStoreRepositoryImpl
import com.example.data.settings.api.AppSettingsRepository
import com.example.data.settings.impl.AppSettingsRepositoryImpl
import com.example.data.user.api.UserDataStoreRepository
import com.example.data.user.impl.UserDataStoreRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val DatastoreModule = module {
    single<UserDataStoreRepository> { UserDataStoreRepositoryImpl(androidContext()) }
    single<AppThemeDataStoreRepository> { AppThemeDataStoreRepositoryImpl(androidContext()) }
    single<AppSettingsRepository> { AppSettingsRepositoryImpl(androidContext()) }
}
