package com.example.core.ui.model

data class UiChat(
    val id: String,
    val user: UiUserData,
    val lastMessage: UiMessage? = null,
    val unreadMessagesCount: Int = 0,
    val isPinned: Boolean = false
)
