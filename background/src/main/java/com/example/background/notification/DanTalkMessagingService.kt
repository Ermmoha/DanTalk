package com.example.background.notification

import com.example.background.service.notification.showIncomingMessageNotification
import com.example.data.settings.api.AppSettingsRepository
import com.example.data.user.api.UserDataStoreRepository
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DanTalkMessagingService : FirebaseMessagingService(), KoinComponent {
    private val firestore: FirebaseFirestore by inject()
    private val userDataStoreRepository: UserDataStoreRepository by inject()
    private val appSettingsRepository: AppSettingsRepository by inject()

    override fun onMessageReceived(message: RemoteMessage) {
        val settings = loadSettings()
        if (!settings.enabled) return

        val data = message.data
        val title = data["title"]
            ?: data["senderName"]
            ?: message.notification?.title
            ?: "New message"
        val messageText = data["body"]
            ?: data["message"]
            ?: message.notification?.body
            ?: "New message"
        val body = if (settings.previewsEnabled) {
            messageText
        } else {
            "Open DanTalk to view this message"
        }
        val notificationKey = data["messageId"]
            ?: data["chatId"]
            ?: System.currentTimeMillis().toString()

        showIncomingMessageNotification(
            context = applicationContext,
            notificationId = notificationKey.hashCode(),
            title = title,
            body = body,
            soundEnabled = settings.soundEnabled,
            vibrationEnabled = settings.vibrationEnabled
        )
    }

    override fun onNewToken(token: String) {
        runBlocking(Dispatchers.IO) {
            registerToken(token)
        }
    }

    private fun loadSettings(): MessagePushSettings =
        runCatching {
            runBlocking(Dispatchers.IO) {
                MessagePushSettings(
                    enabled = appSettingsRepository.notificationsEnabledFlow.first(),
                    previewsEnabled = appSettingsRepository.notificationPreviewsEnabledFlow.first(),
                    soundEnabled = appSettingsRepository.notificationSoundEnabledFlow.first(),
                    vibrationEnabled = appSettingsRepository.notificationVibrationEnabledFlow.first()
                )
            }
        }.getOrDefault(MessagePushSettings())

    private suspend fun registerToken(token: String) {
        val userId = userDataStoreRepository.getUserData.first().id
        if (userId.isBlank()) return

        runCatching {
            firestore.collection("users")
                .document(userId)
                .update("fcmTokens", FieldValue.arrayUnion(token))
                .await()
        }
    }

    private data class MessagePushSettings(
        val enabled: Boolean = true,
        val previewsEnabled: Boolean = true,
        val soundEnabled: Boolean = true,
        val vibrationEnabled: Boolean = true
    )
}
