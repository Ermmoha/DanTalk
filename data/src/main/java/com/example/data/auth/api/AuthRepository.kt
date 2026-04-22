package com.example.data.auth.api

interface AuthRepository {
    suspend fun createUser(email: String, password: String) : String
    suspend fun login(email: String, password: String) : String
    suspend fun signOut()
}