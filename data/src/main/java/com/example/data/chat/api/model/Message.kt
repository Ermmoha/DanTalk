package com.example.data.chat.api.model

data class Message(
    val id: String = "",
    val sender: String = "",
    val message: String = "",
    val read: Boolean = false,
    val isPending: Boolean = false,
    val isEdited: Boolean = false,
    val isPhoto: Boolean = false,
    val isVoice: Boolean = false,
    val mediaDurationMillis: Long = 0L,
    val mediaSizeBytes: Long = 0L,
    val replyToMessageId: String = "",
    val replyToSender: String = "",
    val replyToText: String = "",
    val sentAt: Long = System.currentTimeMillis()
)
