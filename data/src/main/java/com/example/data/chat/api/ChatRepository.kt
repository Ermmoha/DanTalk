package com.example.data.chat.api

import com.example.data.chat.api.model.Chat
import com.example.data.chat.api.model.Message
import com.example.data.user.api.model.UserData
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getChats(userId: String): Flow<List<Chat>>
    suspend fun createChat(userIds: List<String>) : String
    suspend fun deleteChat(id: String)
    suspend fun getChat(id: String): Chat
    suspend fun sendMessage(chatId: String, message: Message)
    suspend fun updateMessage(chatId: String, messageId: String, message: String)
    suspend fun deleteMessage(chatId: String, messageId: String)
    suspend fun readMessage(chatId: String, messageIds: List<String>)
    fun getChatMessages(chatId: String): Flow<List<Message>>
    suspend fun getChatByUserIds(userIds: List<String>): Chat
}
