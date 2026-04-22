package com.example.data.user.impl

import android.util.Log
import com.example.data.user.api.UserRepository
import com.example.data.user.api.model.UserData
import com.example.data.user.impl.entity.UserDataEntity
import com.example.data.user.impl.mapper.toDomain
import com.example.data.user.impl.mapper.toEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal class UserRepositoryImpl(
    private val firestore: FirebaseFirestore,
) : UserRepository {
    override suspend fun createUser(userData: UserData) {
        val entity = userData.toEntity()
        firestore.collection("users").document(userData.id)
            .set(entity)
            .addOnSuccessListener { Log.d("UserRepository", "createUser: $entity") }
            .addOnFailureListener { Log.d("UserRepository", "exception: ${it.message}") }
    }

    override suspend fun getUser(userId: String): UserData =
        try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .get()
                .await()

            val entity = snapshot.toObject(UserDataEntity::class.java)
                ?: throw Exception("User not found")

            entity.toDomain(snapshot.id)
        } catch (e: Exception) {
            Log.d("UserRepository", "exception: ${e.message}")
            throw e
        }

    override suspend fun isValueExists(field: String, value: String): Boolean =
        suspendCoroutine { continuation ->
            firestore.collection("users")
                .whereEqualTo(field, value)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.documents.isNotEmpty())
                        continuation.resume(true)
                    else
                        continuation.resume(false)
                }
                .addOnFailureListener { Log.d("UserRepository", "exception: ${it.message}") }
        }

    override suspend fun getUsersByQuery(query: String): List<UserData> {
        try {
            if (query.isBlank()) return emptyList()
            val snapshot = firestore.collection("users")
                .orderBy("username")
                .startAt(query)
                .endAt("$query\uf8ff")
                .get()
                .await()

            if (snapshot != null && snapshot.documents.isEmpty()) return emptyList()
            val entities = snapshot.documents.mapNotNull {
                it.id to it.toObject(UserDataEntity::class.java)
            }.toMap()

            return entities.map { it.value?.toDomain(it.key) ?: throw Exception("User not found") }
        } catch (e: Exception) {
            Log.d("UserRepository", "exception: ${e.message}")
            throw e
        }
    }

    override suspend fun updateUser(userData: UserData) {
        val data = mapOf(
            "avatar" to userData.avatar,
            "email" to userData.email,
            "username" to userData.username,
            "firstname" to userData.firstname,
            "lastname" to userData.lastname,
            "patronymic" to userData.patronymic
        )
        firestore.collection("users").document(userData.id)
            .update(data)
            .addOnSuccessListener { Log.d("UserRepository", "updateUser: $data") }
            .addOnFailureListener { Log.d("UserRepository", "exception: ${it.message}") }
    }
}