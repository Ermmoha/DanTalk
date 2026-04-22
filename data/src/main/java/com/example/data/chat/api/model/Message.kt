package com.example.data.chat.api.model

data class Message(
    val id: String = "",
    val sender: String = "",
    val message: String = "",
    val read: Boolean = false,
    val isPending: Boolean = true,
    val isEdited: Boolean = false,
    val isPhoto: Boolean = false,
    val sentAt: Long = System.currentTimeMillis()
)
