package com.example.data.user.api

import com.example.data.user.api.model.UserData
import com.google.firebase.firestore.DocumentReference

interface UserRepository {
    suspend fun createUser(userData: UserData)
    suspend fun getUser(userId: String): UserData
    suspend fun isValueExists(field: String, value: String): Boolean
    suspend fun getUsersByQuery(query: String): List<UserData>
    suspend fun updateUser(userData: UserData)
}