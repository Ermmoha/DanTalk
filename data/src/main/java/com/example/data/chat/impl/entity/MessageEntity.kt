package com.example.data.chat.impl.entity

internal data class MessageEntity(
    val sender: String = "",
    val message: String = "",
    val read: Boolean = false,
    val pending: Boolean = true,
    val edited: Boolean = false,
    val photo: Boolean = false,
    val sentAt: Long = System.currentTimeMillis()
)
