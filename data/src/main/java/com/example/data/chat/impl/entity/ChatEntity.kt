package com.example.data.chat.impl.entity

import com.google.firebase.firestore.DocumentReference

internal data class ChatEntity(
    val users: List<DocumentReference> = emptyList(),
)
