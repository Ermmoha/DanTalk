package com.example.background.service

import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.example.background.service.notification.createNotification
import com.example.background.service.notification.showCompletionNotification
import com.example.data.chat.api.ChatRepository
import com.example.data.chat.api.model.Message
import com.example.data.media.api.MediaRepository
import com.example.data.storage.api.StorageRepository
import com.example.data.user.api.UserDataStoreRepository
import com.example.data.user.api.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import java.io.File

class ImageLoadService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) {
            stopSelf()
            return START_NOT_STICKY
        }

        val notification = createNotification(this, "Загрузка медиа")
        startForeground(1, notification)

        when (intent.getStringExtra("action")) {
            "POST_AVATAR" -> {
                val uri = intent.getUriExtra("uri")
                if (uri != null) postAvatar(uri) else stopSelf()
            }

            "POST_MESSAGE_IMAGE" -> {
                val chatId = intent.getStringExtra("chat_id")
                val uri = intent.getUriExtra("uri")
                if (chatId != null && uri != null) {
                    postMessageImage(chatId, uri, intent.messageMetadata(), intent.replyMetadata())
                } else {
                    stopSelf()
                }
            }

            "POST_MESSAGE_VOICE" -> {
                val chatId = intent.getStringExtra("chat_id")
                val uri = intent.getUriExtra("uri")
                val durationMillis = intent.getLongExtra("duration_millis", 0L)
                val sizeBytes = intent.getLongExtra("size_bytes", 0L)
                if (chatId != null && uri != null) {
                    postMessageVoice(
                        chatId = chatId,
                        uri = uri,
                        durationMillis = durationMillis,
                        sizeBytes = sizeBytes,
                        metadata = intent.messageMetadata(),
                        reply = intent.replyMetadata()
                    )
                } else {
                    stopSelf()
                }
            }

            "DOWNLOAD" -> {
                val url = intent.getStringExtra("url")
                if (url != null) download(url) else stopSelf()
            }

            else -> stopSelf()
        }

        return START_NOT_STICKY
    }

    private fun postAvatar(uri: Uri) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val storageRepo = get<StorageRepository>()
                val userDataStoreRepo = get<UserDataStoreRepository>()
                val userRepo = get<UserRepository>()
                val url = storageRepo.postAvatarImage(uri)
                userDataStoreRepo.getUserData.first().let { userData ->
                    userDataStoreRepo.saveUserData(userData.copy(avatar = url))
                    userRepo.updateUser(userData.copy(avatar = url))
                }
                showCompletionNotification(this@ImageLoadService, "Аватар успешно загружен")
                stopSelf()
            } catch (e: Exception) {
                Log.e("ImageLoadService", e.message.toString())
                stopSelf()
            }
        }
    }

    private fun postMessageImage(
        chatId: String,
        uri: Uri,
        metadata: MessageMetadata,
        reply: ReplyMetadata
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val storageRepo = get<StorageRepository>()
            val userDataStoreRepo = get<UserDataStoreRepository>()
            val chatRepo = get<ChatRepository>()
            storageRepo.postMessageImage(uri)
                .onSuccess { url ->
                    runCatching {
                        val message = Message(
                            id = metadata.messageId,
                            sender = userDataStoreRepo.getUserData.first().id,
                            message = url,
                            isPhoto = true,
                            sentAt = metadata.sentAt
                        ).withReply(reply)
                        chatRepo.sendMessage(chatId, message)
                    }.onSuccess {
                        showCompletionNotification(this@ImageLoadService, "Изображение отправлено")
                    }.onFailure {
                        notifyMediaUploadFailed(metadata.messageId)
                        Log.e("ImageLoadService", it.message.toString())
                        showCompletionNotification(this@ImageLoadService, "Изображение не отправлено")
                    }
                    stopSelf()
                }
                .onFailure {
                    notifyMediaUploadFailed(metadata.messageId)
                    Log.e("ImageLoadService", it.message.toString())
                    showCompletionNotification(this@ImageLoadService, "Изображение не отправлено")
                    stopSelf()
                }
        }
    }

    private fun postMessageVoice(
        chatId: String,
        uri: Uri,
        durationMillis: Long,
        sizeBytes: Long,
        metadata: MessageMetadata,
        reply: ReplyMetadata
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val storageRepo = get<StorageRepository>()
            val userDataStoreRepo = get<UserDataStoreRepository>()
            val chatRepo = get<ChatRepository>()
            storageRepo.postMessageVoice(uri)
                .onSuccess { url ->
                    runCatching {
                        val message = Message(
                            id = metadata.messageId,
                            sender = userDataStoreRepo.getUserData.first().id,
                            message = url,
                            isVoice = true,
                            mediaDurationMillis = durationMillis,
                            mediaSizeBytes = sizeBytes,
                            sentAt = metadata.sentAt
                        ).withReply(reply)
                        chatRepo.sendMessage(chatId, message)
                    }.onSuccess {
                        showCompletionNotification(this@ImageLoadService, "Голосовое сообщение отправлено")
                    }.onFailure {
                        notifyMediaUploadFailed(metadata.messageId)
                        Log.e("ImageLoadService", it.message.toString())
                        showCompletionNotification(this@ImageLoadService, "Голосовое сообщение не отправлено")
                    }
                    cleanupCacheFile(uri)
                    stopSelf()
                }
                .onFailure {
                    notifyMediaUploadFailed(metadata.messageId)
                    Log.e("ImageLoadService", it.message.toString())
                    showCompletionNotification(this@ImageLoadService, "Голосовое сообщение не отправлено")
                    cleanupCacheFile(uri)
                    stopSelf()
                }
        }
    }

    private fun download(url: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val storageRepo = get<StorageRepository>()
                val mediaRepo = get<MediaRepository>()
                val image = storageRepo.downloadAvatarImage(url)
                mediaRepo.saveImageToGallery(image).let { uri ->
                    if (uri != null) {
                        showCompletionNotification(
                            this@ImageLoadService,
                            "Изображение сохранено на устройство"
                        )
                    }
                }
                stopSelf()
            } catch (e: Exception) {
                Log.e("ImageLoadService", e.message.toString())
                stopSelf()
            }
        }
    }

    private fun Intent.getUriExtra(name: String): Uri? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getParcelableExtra(name, Uri::class.java)
        } else {
            @Suppress("DEPRECATION")
            getParcelableExtra(name)
        }

    private fun Intent.replyMetadata(): ReplyMetadata =
        ReplyMetadata(
            messageId = getStringExtra("reply_to_message_id").orEmpty(),
            sender = getStringExtra("reply_to_sender").orEmpty(),
            text = getStringExtra("reply_to_text").orEmpty()
        )

    private fun Intent.messageMetadata(): MessageMetadata =
        MessageMetadata(
            messageId = getStringExtra(ImageLoadServiceStarter.EXTRA_MESSAGE_ID).orEmpty(),
            sentAt = getLongExtra(ImageLoadServiceStarter.EXTRA_SENT_AT, System.currentTimeMillis())
        )

    private fun Message.withReply(reply: ReplyMetadata): Message =
        if (reply.messageId.isBlank()) {
            this
        } else {
            copy(
                replyToMessageId = reply.messageId,
                replyToSender = reply.sender,
                replyToText = reply.text
            )
        }

    private fun cleanupCacheFile(uri: Uri) {
        if (uri.scheme != "file") return
        runCatching { File(uri.path.orEmpty()).delete() }
    }

    private fun notifyMediaUploadFailed(messageId: String) {
        if (messageId.isBlank()) return
        sendBroadcast(
            Intent(ImageLoadServiceStarter.ACTION_MEDIA_UPLOAD_FAILED)
                .setPackage(packageName)
                .putExtra(ImageLoadServiceStarter.EXTRA_MESSAGE_ID, messageId)
        )
    }

    private data class MessageMetadata(
        val messageId: String,
        val sentAt: Long
    )

    private data class ReplyMetadata(
        val messageId: String,
        val sender: String,
        val text: String
    )

    override fun onBind(intent: Intent?): IBinder? = null
}
