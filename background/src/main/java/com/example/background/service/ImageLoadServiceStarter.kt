package com.example.background.service

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat

object ImageLoadServiceStarter {
    const val ACTION_MEDIA_UPLOAD_FAILED = "com.example.background.MEDIA_UPLOAD_FAILED"
    const val EXTRA_MESSAGE_ID = "message_id"
    const val EXTRA_SENT_AT = "sent_at"

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
        messageId: String,
        sentAt: Long,
        replyToMessageId: String = "",
        replyToSender: String = "",
        replyToText: String = ""
    ) {
        val intent = Intent(context, ImageLoadService::class.java).apply {
            putExtra("action", "POST_MESSAGE_IMAGE")
            putExtra("chat_id", chatId)
            putExtra("uri", uri)
            putExtra(EXTRA_MESSAGE_ID, messageId)
            putExtra(EXTRA_SENT_AT, sentAt)
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
        messageId: String,
        sentAt: Long,
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
            putExtra(EXTRA_MESSAGE_ID, messageId)
            putExtra(EXTRA_SENT_AT, sentAt)
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
