package com.example.data.di

import com.example.data.media.api.MediaRepository
import com.example.data.media.impl.MediaRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val MediaModule = module {
    single<MediaRepository> { MediaRepositoryImpl(androidContext()) }
}