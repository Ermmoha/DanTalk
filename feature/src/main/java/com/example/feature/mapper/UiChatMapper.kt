package com.example.feature.mapper

import com.example.core.ui.model.UiChat
import com.example.core.ui.model.UiMessage
import com.example.data.chat.api.model.Chat

internal fun Chat.toUi(currentUserId: String, messages: List<UiMessage> = emptyList()) =
    UiChat(
        id = id,
        user = users.first { it.id != currentUserId }.toUi(),
        lastMessage = messages.firstOrNull(),
        unreadMessagesCount = messages.filter { !it.read && !it.isCurrentUserMessage }.size
    )