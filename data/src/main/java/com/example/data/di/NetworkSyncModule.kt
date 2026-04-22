package com.example.data.di

import com.example.data.network_sync.NetworkSyncManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val NetworkSyncModule = module {
    single<NetworkSyncManager> { NetworkSyncManager(androidContext(), get()) }
}