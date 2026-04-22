package com.example.data.user.api

import com.example.data.user.api.model.UserData
import kotlinx.coroutines.flow.Flow

interface UserDataStoreRepository {
    val getUserData: Flow<UserData>
    suspend fun saveUserData(userData: UserData)
    suspend fun clearUserData()
}