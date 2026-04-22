package com.example.data.di

import com.example.data.auth.api.AuthRepository
import com.example.data.auth.impl.AuthRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import org.koin.dsl.module

val AuthModule = module {
    single<FirebaseAuth> { FirebaseAuth.getInstance() }
    single<AuthRepository> { AuthRepositoryImpl(get()) }
}