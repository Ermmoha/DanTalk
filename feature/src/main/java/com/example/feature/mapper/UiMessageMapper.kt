package com.example.feature.mapper

import com.example.core.ui.model.UiMessage
import com.example.core.util.toDateString
import com.example.core.util.toTimeString
import com.example.data.chat.api.model.Message

internal fun Message.toUi(currentUserId: String) =
    UiMessage(
        id = id,
        isCurrentUserMessage = sender == currentUserId,
        message = message,
        read = read,
        isPending = isPending,
        isEdited = isEdited,
        isPhoto = isPhoto,
        sentAt = sentAt,
        date = sentAt.toDateString(),
        time = sentAt.toTimeString()
    )

internal fun List<Message>.toUi(currentUserId: String) =
    map { it.toUi(currentUserId) }
