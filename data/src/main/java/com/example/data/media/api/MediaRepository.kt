package com.example.data.media.api

import android.content.Context
import android.net.Uri

interface MediaRepository {
    suspend fun saveImageToGallery(
        image: ByteArray,
        fileName: String = "image_${System.currentTimeMillis()}.jpg"
    ) : Uri?
}