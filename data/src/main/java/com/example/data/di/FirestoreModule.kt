package com.example.data.di

import android.util.Log
import com.example.data.chat.api.ChatRepository
import com.example.data.chat.impl.ChatRepositoryImpl
import com.example.data.user.api.UserRepository
import com.example.data.user.impl.UserRepositoryImpl
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import org.koin.dsl.module

val FirestoreModule = module {
    single<FirebaseFirestore> {
        FirebaseFirestore.getInstance().apply {
            persistentCacheIndexManager?.apply {
                enableIndexAutoCreation()
            } ?: Log.w("Firestore", "IndexManages is null")
        }
    }
    single<UserRepository> { UserRepositoryImpl(get()) }
    single<ChatRepository> { ChatRepositoryImpl(get()) }
}