package com.example.background.notification

import com.example.data.user.api.UserDataStoreRepository
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FcmTokenRegistrar(
    private val firestore: FirebaseFirestore,
    private val userDataStoreRepository: UserDataStoreRepository
) {
    fun observeUserAndRegisterToken(scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            userDataStoreRepository.getUserData
                .map { it.id }
                .distinctUntilChanged()
                .collect { userId ->
                    if (userId.isBlank()) return@collect
                    registerToken(userId)
                }
        }
    }

    private suspend fun registerToken(userId: String) {
        runCatching {
            val token = FirebaseMessaging.getInstance().token.await()
            firestore.collection("users")
                .document(userId)
                .update("fcmTokens", FieldValue.arrayUnion(token))
                .await()
        }
    }
}
