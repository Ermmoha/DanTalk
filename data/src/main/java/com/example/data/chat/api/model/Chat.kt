package com.example.data.chat.api.model

import com.example.data.user.api.model.UserData

data class Chat(
    val id: String = "",
    val users: List<UserData> = emptyList(),
)