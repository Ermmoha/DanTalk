package com.example.data.di

import com.example.core.util.SupabaseConst
import com.example.data.storage.api.StorageRepository
import com.example.data.storage.impl.StorageRepositoryImpl
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val StorageModule = module {
    single {
        createSupabaseClient(
            supabaseUrl = SupabaseConst.URL,
            supabaseKey = SupabaseConst.API_KEY
        ) {
            install(Storage)
        }
    }
    single<StorageRepository> {
        StorageRepositoryImpl(
            client = get(),
            context = androidContext())
    }
}