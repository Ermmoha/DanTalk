package com.example.feature.main.chat.store

import android.content.Context
import android.net.Uri
import com.arkivanov.mvikotlin.core.store.Store
import com.example.core.ui.model.UiChat
import com.example.core.ui.model.UiMessage
import com.example.feature.main.chat.store.ChatStore.*
import com.example.data.user.api.model.UserData
import com.example.feature.main.chat.model.MessageListItem

interface ChatStore : Store<Intent, State, Label> {

    sealed interface Intent {
        class OnMessageChange(val message: String) : Intent
        data object SendMessage : Intent
        class SendPhoto(val context: Context, val uri: Uri) : Intent
        class SendVoice(
            val context: Context,
            val uri: Uri,
            val durationMillis: Long,
            val sizeBytes: Long
        ) : Intent
        class ReadMessage(val ids: List<String>) : Intent
        class StartEditMessage(val messageId: String, val message: String) : Intent
        data object CancelEditMessage : Intent
        class StartReplyMessage(val message: UiMessage) : Intent
        data object CancelReplyMessage : Intent
        class DeleteMessage(val messageId: String) : Intent
        class MediaUploadFailed(val messageId: String) : Intent
        class DownloadImage(val context: Context, val url: String) : Intent
        data object NavigateBack : Intent
    }

    data class State(
        val chat: UiChat? = null,
        val messages: List<MessageListItem> = emptyList(),
        val remoteMessages: List<UiMessage> = emptyList(),
        val pendingMessages: List<UiMessage> = emptyList(),
        val currentMessage: String = "",
        val currentUser: UserData = UserData(),
        val editingMessageId: String? = null,
        val replyingToMessage: UiMessage? = null
    )

    sealed interface Label {
        data object NavigateBack : Label
    }
}
