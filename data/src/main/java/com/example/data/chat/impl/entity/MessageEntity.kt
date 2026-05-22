package com.example.data.chat.impl.entity

internal data class MessageEntity(
    val sender: String = "",
    val message: String = "",
    val read: Boolean = false,
    val pending: Boolean = false,
    val edited: Boolean = false,
    val photo: Boolean = false,
    val voice: Boolean = false,
    val mediaDurationMillis: Long = 0L,
    val mediaSizeBytes: Long = 0L,
    val replyToMessageId: String = "",
    val replyToSender: String = "",
    val replyToText: String = "",
    val sentAt: Long = System.currentTimeMillis()
)
