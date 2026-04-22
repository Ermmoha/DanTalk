package com.example.data.storage.api

import android.net.Uri

interface StorageRepository {
    suspend fun postAvatarImage(uri: Uri): String
    suspend fun downloadAvatarImage(url: String): ByteArray
    suspend fun postMessageImage(uri: Uri): Result<String>
}