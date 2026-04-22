package com.example.background.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.user.api.UserDataStoreRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.tasks.await

class PendingSyncWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val firestore: FirebaseFirestore,
    private val userDataStoreRepository: UserDataStoreRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val currentUserId =
            userDataStoreRepository.getUserData.firstOrNull()?.id ?: return Result.failure()

        val currentUserRef = firestore.collection("users")
            .document(currentUserId)
            .get()
            .await()
            .reference

        return try {
            val chats = firestore.collection("chats")
                .whereArrayContains("users", currentUserRef)
                .get()
                .await()
                .documents
            Log.d("PendingSyncWorker", "Found ${chats.size} chats")

            chats.forEach { chat ->
                val pendingMessages = firestore.collection("chats")
                    .document(chat.id)
                    .collection("messages")
                    .whereEqualTo("sender", currentUserId)
                    .whereEqualTo("pending", true)
                    .get()
                    .await()

                for (document in pendingMessages.documents) {
                    document.reference.update("pending", false).await()
                }
            }
            Result.success()
        } catch (e: Exception) {
            Log.e("PendingSyncWorker", e.message.toString())
            Result.retry()
        }
    }
}