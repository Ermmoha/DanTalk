package com.example.core.ui.model

data class UiMessage(
    val id: String,
    val isCurrentUserMessage: Boolean,
    val message: String,
    val read: Boolean,
    val isPending: Boolean,
    val isEdited: Boolean,
    val isPhoto: Boolean,
    val sentAt: Long,
    val date: String,
    val time: String
)
