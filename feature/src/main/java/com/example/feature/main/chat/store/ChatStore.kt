package com.example.feature.main.chat.store

import android.content.Context
import android.net.Uri
import com.arkivanov.mvikotlin.core.store.Store
import com.example.core.ui.model.UiChat
import com.example.feature.main.chat.store.ChatStore.*
import com.example.data.chat.api.model.Chat
import com.example.data.user.api.model.UserData
import com.example.feature.main.chat.model.MessageListItem

interface ChatStore : Store<Intent, State, Label> {

    sealed interface Intent {
        class OnMessageChange(val message: String) : Intent
        data object SendMessage : Intent
        class SendPhoto(val context: Context, val uri: Uri) : Intent
        class ReadMessage(val ids: List<String>) : Intent
        class StartEditMessage(val messageId: String, val message: String) : Intent
        data object CancelEditMessage : Intent
        class DeleteMessage(val messageId: String) : Intent
        class DownloadImage(val context: Context, val url: String) : Intent
        data object NavigateBack : Intent
    }

    data class State(
        val chat: UiChat? = null,
        val messages: List<MessageListItem> = emptyList(),
        val currentMessage: String = "",
        val currentUser: UserData = UserData(),
        val editingMessageId: String? = null
    )

    sealed interface Label {
        data object NavigateBack : Label
    }
}
