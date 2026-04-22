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

class ImageLoadService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.getStringExtra("action")

        val notification = createNotification(this, "Загрузка изображения")
        startForeground(1, notification)

        when (action) {
            "POST_AVATAR" -> {
                val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra("uri", Uri::class.java)
                } else {
                    intent.getParcelableExtra("uri")
                }
                if (uri != null)
                    postAvatar(uri)
                else
                    stopSelf()
            }

            "POST_MESSAGE_IMAGE" -> {
                val chatId = intent.getStringExtra("chat_id")
                val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra("uri", Uri::class.java)
                } else {
                    intent.getParcelableExtra("uri")
                }
                if (chatId != null && uri != null)
                    postMessageImage(chatId, uri)
                else
                    stopSelf()
            }

            "DOWNLOAD" -> {
                val url = intent.getStringExtra("url")
                if (url != null)
                    download(url)
                else
                    stopSelf()
            }
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

    private fun postMessageImage(chatId: String, uri: Uri) {
        CoroutineScope(Dispatchers.IO).launch {
            val storageRepo = get<StorageRepository>()
            val userDataStoreRepo = get<UserDataStoreRepository>()
            val chatRepo = get<ChatRepository>()
            storageRepo.postMessageImage(uri)
                .onSuccess {
                    val message = Message(
                        sender = userDataStoreRepo.getUserData.first().id,
                        message = it,
                        isPhoto = true
                    )
                    chatRepo.sendMessage(chatId, message)
                    showCompletionNotification(this@ImageLoadService, "Изображение успешно загружено")
                    stopSelf()
                }
                .onFailure {
                    showCompletionNotification(this@ImageLoadService, "Изображение не загружено")
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
                    if (uri != null) showCompletionNotification(
                        this@ImageLoadService,
                        "Изображение успешно загружено на устройство"
                    )
                }
                stopSelf()
            } catch (e: Exception) {
                Log.e("ImageLoadService", e.message.toString())
                stopSelf()
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}