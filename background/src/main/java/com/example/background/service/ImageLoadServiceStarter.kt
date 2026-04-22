package com.example.background.service

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat

object ImageLoadServiceStarter {
    fun postAvatar(context: Context, uri: Uri) {
        val intent = Intent(context, ImageLoadService::class.java).apply {
            putExtra("action", "POST_AVATAR")
            putExtra("uri", uri)
        }
        ContextCompat.startForegroundService(context, intent)
    }

    fun postMessageImage(context: Context, chatId: String, uri: Uri) {
        val intent = Intent(context, ImageLoadService::class.java).apply {
            putExtra("action", "POST_MESSAGE_IMAGE")
            putExtra("chat_id", chatId)
            putExtra("uri", uri)
        }
        ContextCompat.startForegroundService(context, intent)
    }

    fun download(context: Context, url: String) {
        val intent = Intent(context, ImageLoadService::class.java).apply {
            putExtra("action", "DOWNLOAD")
            putExtra("url", url)
        }
        ContextCompat.startForegroundService(context, intent)
    }
}