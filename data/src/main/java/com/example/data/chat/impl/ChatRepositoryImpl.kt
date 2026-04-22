package com.example.data.chat.impl

import android.util.Log
import com.example.data.chat.api.ChatRepository
import com.example.data.chat.api.model.Chat
import com.example.data.chat.api.model.Message
import com.example.data.chat.impl.entity.ChatEntity
import com.example.data.chat.impl.entity.MessageEntity
import com.example.data.chat.impl.mapper.toDomain
import com.example.data.chat.impl.mapper.toEntity
import com.example.data.user.api.model.UserData
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

internal class ChatRepositoryImpl(
    private val firestore: FirebaseFirestore,
) : ChatRepository {
    override fun getChats(userId: String): Flow<List<Chat>> = callbackFlow {
        val userRef = getUserReference(userId)
        val listener = firestore.collection("chats")
            .whereArrayContains("users", userRef)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }
                if (snapshot == null) return@addSnapshotListener
                val chatEntities = snapshot.documents.mapNotNull {
                    it.id to it.toObject(ChatEntity::class.java)
                }.toMap()
                CoroutineScope(Dispatchers.IO).launch {
                    val chats = chatEntities.map { entity ->
                        entity.value?.toDomain(
                            entity.key,
                            getChatUsers(entity.value?.users ?: emptyList())
                        ) ?: throw Exception("Failed to get chat")
                    }
                    trySend(chats)
                }
            }
        awaitClose { listener.remove() }
    }.distinctUntilChanged()

    override suspend fun createChat(userIds: List<String>): String {
        val chatId = userIds.sorted().joinToString("")
        val users = listOf(
            firestore.collection("users").document(userIds[0]),
            firestore.collection("users").document(userIds[1])
        )

        val entity = ChatEntity(users)
        firestore.collection("chats")
            .document(chatId)
            .set(entity)
            .await()

        return chatId
    }

    override suspend fun deleteChat(id: String) {
        firestore.collection("chats")
            .document(id)
            .delete()
            .await()

        firestore.collection("chats")
            .document(id)
            .collection("messages")
            .get()
            .await()
            .let { snapshot ->
                snapshot.documents.forEach { doc ->
                    doc.reference.delete().await()
                }
            }
    }

    override suspend fun getChat(id: String): Chat =
        try {
            val snapshot = firestore.collection("chats")
                .document(id)
                .get()
                .await()

            val chatEntity = snapshot.toObject(ChatEntity::class.java)
                ?: throw Exception("Failed to get chat")

            chatEntity.toDomain(snapshot.id, getChatUsers(chatEntity.users))
        } catch (e: Exception) {
            Log.d("Firestore", "Failed to get chat $e")
            throw e
        }

    override suspend fun sendMessage(chatId: String, message: Message) {
        val docRef = firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .add(message.toEntity())
            .await()

        docRef.update("pending", false).await()
    }

    override suspend fun updateMessage(chatId: String, messageId: String, message: String) {
        firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .document(messageId)
            .update(
                mapOf(
                    "message" to message,
                    "edited" to true
                )
            )
            .await()
    }

    override suspend fun deleteMessage(chatId: String, messageId: String) {
        firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .document(messageId)
            .delete()
            .await()
    }

    override fun getChatMessages(chatId: String): Flow<List<Message>> = callbackFlow {
        val listener = firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.d("Firestore", "Failed to get messages $e")
                    return@addSnapshotListener
                }
                val messages = snapshot?.documents?.mapNotNull {
                    it.toObject(MessageEntity::class.java)?.toDomain(it.id)
                }?.sortedBy { it.sentAt }?.reversed() ?: emptyList()
                trySend(messages)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun readMessage(chatId: String, messageIds: List<String>) {
        messageIds.forEach { messageId ->
            firestore.collection("chats")
                .document(chatId)
                .collection("messages")
                .document(messageId)
                .update("read", true)
                .await()
        }
    }

    override suspend fun getChatByUserIds(userIds: List<String>): Chat =
        try {
            val documentId = userIds.sorted().joinToString("")
            val snapshot = firestore.collection("chats")
                .document(documentId)
                .get()
                .await()

            val entity = snapshot.toObject(ChatEntity::class.java)
                ?: throw Exception("Failed to get chat")

            entity.toDomain(snapshot.id, getChatUsers(entity.users))
        } catch (e: Exception) {
            Log.d("Firestore", "Failed to get chat $e")
            throw e
        }

    private suspend fun getChatUsers(refs: List<DocumentReference>): List<UserData> =
        refs.map { ref ->
            val snapshot = firestore.collection("users")
                .document(ref.id)
                .get()
                .await()
            snapshot.toObject(UserData::class.java)?.copy(id = snapshot.id)
                ?: throw Exception("Failed to get user")
        }


    private suspend fun getUserReference(userId: String): DocumentReference =
        firestore.collection("users")
            .document(userId)
            .get()
            .await()
            .reference
}
