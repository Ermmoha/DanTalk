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
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
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
                val chatEntities = snapshot.documents.mapNotNull { document ->
                    document.toObject(ChatEntity::class.java)?.let { document.id to it }
                }
                launch(Dispatchers.IO) {
                    val chats = chatEntities.map { entity ->
                        entity.second.toDomain(
                            entity.first,
                            getChatUsers(entity.second.users)
                        )
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
        val messagesRef = firestore.collection("chats")
            .document(chatId)
            .collection("messages")

        val messageId = message.id.ifBlank { messagesRef.document().id }
        messagesRef
            .document(messageId)
            .set(message.toEntity())
            .await()
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
        runCatching {
            firestore.collection("chats")
                .document(chatId)
                .collection("messages")
                .document(messageId)
                .delete()
                .await()
        }.onFailure {
            Log.d("Firestore", "Failed to delete message $it")
        }
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
                launch(Dispatchers.Default) {
                    val messages = snapshot?.documents?.mapNotNull {
                        it.toObject(MessageEntity::class.java)?.toDomain(it.id)
                    }?.sortedByDescending { it.sentAt } ?: emptyList()
                    trySend(messages)
                }
            }
        awaitClose { listener.remove() }
    }

    override suspend fun readMessage(chatId: String, messageIds: List<String>) {
        messageIds
            .distinct()
            .chunked(450)
            .forEach { chunk ->
                runCatching {
                    val batch = firestore.batch()
                    chunk.forEach { messageId ->
                        val reference = firestore.collection("chats")
                            .document(chatId)
                            .collection("messages")
                            .document(messageId)
                        batch.update(reference, "read", true)
                    }
                    batch.commit().await()
                }.onFailure {
                    Log.d("Firestore", "Failed to mark messages as read $it")
                }
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

    private suspend fun getChatUsers(refs: List<DocumentReference>): List<UserData> {
        if (refs.isEmpty()) return emptyList()

        val users = refs
            .distinctBy { it.id }
            .chunked(10)
            .flatMap { chunk ->
                firestore.collection("users")
                    .whereIn(FieldPath.documentId(), chunk.map { it.id })
                    .get()
                    .await()
                    .documents
            }
            .associateBy { it.id }

        return refs.map { ref ->
            users[ref.id]?.toObject(UserData::class.java)?.copy(id = ref.id)
                ?: throw Exception("Failed to get user")
        }
    }

    private suspend fun getUserReference(userId: String): DocumentReference =
        firestore.collection("users")
            .document(userId)
            .get()
            .await()
            .reference
}
