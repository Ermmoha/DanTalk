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

    fun postMessageImage(
        context: Context,
        chatId: String,
        uri: Uri,
        replyToMessageId: String = "",
        replyToSender: String = "",
        replyToText: String = ""
    ) {
        val intent = Intent(context, ImageLoadService::class.java).apply {
            putExtra("action", "POST_MESSAGE_IMAGE")
            putExtra("chat_id", chatId)
            putExtra("uri", uri)
            putReplyExtras(replyToMessageId, replyToSender, replyToText)
        }
        ContextCompat.startForegroundService(context, intent)
    }

    fun postMessageVoice(
        context: Context,
        chatId: String,
        uri: Uri,
        durationMillis: Long,
        sizeBytes: Long,
        replyToMessageId: String = "",
        replyToSender: String = "",
        replyToText: String = ""
    ) {
        val intent = Intent(context, ImageLoadService::class.java).apply {
            putExtra("action", "POST_MESSAGE_VOICE")
            putExtra("chat_id", chatId)
            putExtra("uri", uri)
            putExtra("duration_millis", durationMillis)
            putExtra("size_bytes", sizeBytes)
            putReplyExtras(replyToMessageId, replyToSender, replyToText)
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

    private fun Intent.putReplyExtras(
        replyToMessageId: String,
        replyToSender: String,
        replyToText: String
    ) {
        putExtra("reply_to_message_id", replyToMessageId)
        putExtra("reply_to_sender", replyToSender)
        putExtra("reply_to_text", replyToText)
    }
}
