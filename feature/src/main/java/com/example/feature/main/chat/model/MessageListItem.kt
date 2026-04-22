package com.example.feature.main.chat.model

import com.example.core.ui.model.UiMessage

sealed class MessageListItem {
    data class MessageItem(val message: UiMessage) : MessageListItem()
    data class DateItem(val date: String) : MessageListItem()
}