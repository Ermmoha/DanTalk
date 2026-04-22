package com.example.background.notification

import android.content.Context
import com.example.background.service.notification.showIncomingMessageNotification
import com.example.data.settings.api.AppSettingsRepository
import com.example.data.user.api.UserDataStoreRepository
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class IncomingMessageNotifier(
    private val context: Context,
    private val firestore: FirebaseFirestore,
    private val userDataStoreRepository: UserDataStoreRepository,
    private val appSettingsRepository: AppSettingsRepository
) {
    private var chatsListener: ListenerRegistration? = null
    private val chatMessageListeners = mutableMapOf<String, ListenerRegistration>()
    private val lastMessageByChat = mutableMapOf<String, String>()
    private val initializedChats = mutableSetOf<String>()
    private val usernames = mutableMapOf<String, String>()

    fun observeIncomingMessages(scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            combine(
                userDataStoreRepository.getUserData
                    .map { it.id }
                    .distinctUntilChanged(),
                appSettingsRepository.notificationsEnabledFlow.distinctUntilChanged()
            ) { userId, enabled ->
                userId to enabled
            }.collect { (userId, enabled) ->
                clearListeners()
                if (userId.isBlank() || !enabled) return@collect
                observeChats(scope, userId)
            }
        }
    }

    private fun observeChats(scope: CoroutineScope, currentUserId: String) {
        val currentUserRef = firestore.collection("users").document(currentUserId)

        chatsListener = firestore.collection("chats")
            .whereArrayContains("users", currentUserRef)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot == null) return@addSnapshotListener

                val chatIds = snapshot.documents.map { it.id }.toSet()
                val removed = chatMessageListeners.keys - chatIds
                removed.forEach { chatId ->
                    chatMessageListeners.remove(chatId)?.remove()
                    lastMessageByChat.remove(chatId)
                    initializedChats.remove(chatId)
                }

                snapshot.documents.forEach { chatDoc ->
                    if (chatMessageListeners.containsKey(chatDoc.id)) return@forEach

                    val partnerId = (chatDoc.get("users") as? List<*>)
                        ?.filterIsInstance<DocumentReference>()
                        ?.firstOrNull { it.id != currentUserId }
                        ?.id

                    if (!partnerId.isNullOrBlank()) {
                        cacheUsername(scope, partnerId)
                    }

                    observeLastMessage(
                        chatId = chatDoc.id,
                        currentUserId = currentUserId,
                        partnerId = partnerId
                    )
                }
            }
    }

    private fun cacheUsername(scope: CoroutineScope, userId: String) {
        if (usernames[userId] != null) return
        scope.launch(Dispatchers.IO) {
            runCatching {
                firestore.collection("users")
                    .document(userId)
                    .get()
                    .await()
                    .getString("username")
            }.onSuccess { username ->
                if (!username.isNullOrBlank()) usernames[userId] = username
            }
        }
    }

    private fun observeLastMessage(
        chatId: String,
        currentUserId: String,
        partnerId: String?
    ) {
        val listener = firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("sentAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(1)
            .addSnapshotListener { snapshot, _ ->
                val messageDoc = snapshot?.documents?.firstOrNull() ?: return@addSnapshotListener
                val messageId = messageDoc.id

                if (!initializedChats.contains(chatId)) {
                    initializedChats.add(chatId)
                    lastMessageByChat[chatId] = messageId
                    return@addSnapshotListener
                }

                if (lastMessageByChat[chatId] == messageId) return@addSnapshotListener
                lastMessageByChat[chatId] = messageId

                val senderId = messageDoc.getString("sender").orEmpty()
                if (senderId == currentUserId) return@addSnapshotListener

                val isPhoto = messageDoc.getBoolean("photo") ?: false
                val body = if (isPhoto) {
                    "Sent a photo"
                } else {
                    messageDoc.getString("message").orEmpty().ifBlank { "New message" }
                }
                val title = partnerId?.let { usernames[it] } ?: "New message"

                showIncomingMessageNotification(
                    context = context,
                    notificationId = (chatId + messageId).hashCode(),
                    title = title,
                    body = body
                )
            }

        chatMessageListeners[chatId] = listener
    }

    private fun clearListeners() {
        chatsListener?.remove()
        chatsListener = null

        chatMessageListeners.values.forEach { it.remove() }
        chatMessageListeners.clear()
        lastMessageByChat.clear()
        initializedChats.clear()
    }
}
