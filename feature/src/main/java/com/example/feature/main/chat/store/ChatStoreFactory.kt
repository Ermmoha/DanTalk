package com.example.feature.main.chat.store

import android.util.Log
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapperScope
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutorScope
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import com.example.background.service.ImageLoadServiceStarter
import com.example.core.ui.model.UiChat
import com.example.core.ui.model.UiMessage
import com.example.core.util.toDateString
import com.example.data.chat.api.ChatRepository
import com.example.data.user.api.model.UserData
import com.example.feature.main.chat.model.MessageActionStateReducer
import com.example.feature.main.chat.model.MessageDraftFactory
import com.example.feature.main.chat.model.MessageListItem
import com.example.feature.main.chat.model.ReplyMetadata
import com.example.feature.main.chat.store.ChatStore.Intent
import com.example.feature.main.chat.store.ChatStore.Label
import com.example.feature.main.chat.store.ChatStore.State
import com.example.feature.mapper.toUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class ChatStoreFactory(
    private val factory: StoreFactory,
    private val chatRepository: ChatRepository,
    private val userDataFlow: Flow<UserData>,
    private val chatId: String,
) {
    private sealed interface Action {
        class GetChat(val chat: UiChat) : Action
        class GetMessages(val messages: List<UiMessage>) : Action
        class SetUser(val user: UserData) : Action
    }

    private sealed interface Msg {
        class GetChat(val chat: UiChat) : Msg
        class GetMessages(val messages: List<UiMessage>) : Msg
        class OnMessageChange(val message: String) : Msg
        class AddPendingMessage(val message: UiMessage) : Msg
        class RemovePendingMessage(val messageId: String) : Msg
        data object ClearMessage : Msg
        class StartEditMessage(val messageId: String, val message: String) : Msg
        data object CancelEditMessage : Msg
        class StartReplyMessage(val message: UiMessage) : Msg
        data object CancelReplyMessage : Msg
        class SetUser(val user: UserData) : Msg
    }

    fun create(): ChatStore =
        object : ChatStore,
            Store<Intent, State, Label> by factory.create<Intent, Action, Msg, State, Label>(
                name = "ChatStore",
                initialState = State(),
                bootstrapper = coroutineBootstrapper {
                    launch {
                        userDataFlow.collect { user ->
                            dispatch(Action.SetUser(user))
                            getChat(chatId, user.id)
                        }
                    }
                },
                executorFactory = coroutineExecutorFactory {
                    onAction<Action.GetChat> { dispatch(Msg.GetChat(it.chat)) }
                    onAction<Action.GetMessages> { dispatch(Msg.GetMessages(it.messages)) }
                    onAction<Action.SetUser> { dispatch(Msg.SetUser(it.user)) }
                    onIntent<Intent.OnMessageChange> { dispatch(Msg.OnMessageChange(it.message)) }
                    onIntent<Intent.SendMessage> { sendMessage() }
                    onIntent<Intent.StartEditMessage> {
                        dispatch(Msg.StartEditMessage(it.messageId, it.message))
                    }
                    onIntent<Intent.CancelEditMessage> { dispatch(Msg.CancelEditMessage) }
                    onIntent<Intent.StartReplyMessage> {
                        dispatch(Msg.StartReplyMessage(it.message))
                    }
                    onIntent<Intent.CancelReplyMessage> { dispatch(Msg.CancelReplyMessage) }
                    onIntent<Intent.DeleteMessage> { deleteMessage(it.messageId) }
                    onIntent<Intent.SendPhoto> {
                        val reply = state().replyMetadata()
                        val pendingMessage = state().pendingMediaMessage(
                            content = it.uri.toString(),
                            isPhoto = true
                        )
                        dispatch(Msg.AddPendingMessage(pendingMessage))
                        dispatch(Msg.CancelReplyMessage)
                        try {
                            ImageLoadServiceStarter.postMessageImage(
                                context = it.context,
                                chatId = chatId,
                                uri = it.uri,
                                messageId = pendingMessage.id,
                                sentAt = pendingMessage.sentAt,
                                replyToMessageId = reply.messageId,
                                replyToSender = reply.sender,
                                replyToText = reply.text
                            )
                        } catch (e: Exception) {
                            dispatch(Msg.RemovePendingMessage(pendingMessage.id))
                            Log.d("ChatStore", e.message.toString())
                        }
                    }
                    onIntent<Intent.SendVoice> {
                        val reply = state().replyMetadata()
                        val pendingMessage = state().pendingMediaMessage(
                            content = it.uri.toString(),
                            isVoice = true,
                            durationMillis = it.durationMillis,
                            sizeBytes = it.sizeBytes
                        )
                        dispatch(Msg.AddPendingMessage(pendingMessage))
                        dispatch(Msg.CancelReplyMessage)
                        try {
                            ImageLoadServiceStarter.postMessageVoice(
                                context = it.context,
                                chatId = chatId,
                                uri = it.uri,
                                durationMillis = it.durationMillis,
                                sizeBytes = it.sizeBytes,
                                messageId = pendingMessage.id,
                                sentAt = pendingMessage.sentAt,
                                replyToMessageId = reply.messageId,
                                replyToSender = reply.sender,
                                replyToText = reply.text
                            )
                        } catch (e: Exception) {
                            dispatch(Msg.RemovePendingMessage(pendingMessage.id))
                            Log.d("ChatStore", e.message.toString())
                        }
                    }
                    onIntent<Intent.MediaUploadFailed> {
                        dispatch(Msg.RemovePendingMessage(it.messageId))
                    }
                    onIntent<Intent.ReadMessage> { readMessage(it.ids) }
                    onIntent<Intent.DownloadImage> {
                        ImageLoadServiceStarter.download(
                            context = it.context,
                            url = it.url
                        )
                    }
                    onIntent<Intent.NavigateBack> { publish(Label.NavigateBack) }
                },
                reducer = { msg ->
                    when (msg) {
                        is Msg.GetChat -> copy(chat = msg.chat)
                        is Msg.GetMessages -> withRemoteMessages(msg.messages)
                        is Msg.OnMessageChange -> copy(currentMessage = msg.message)
                        is Msg.AddPendingMessage -> withPendingMessage(msg.message)
                        is Msg.RemovePendingMessage -> withoutPendingMessage(msg.messageId)
                        is Msg.ClearMessage -> copy(
                            currentMessage = "",
                            editingMessageId = null,
                            replyingToMessage = null
                        )
                        is Msg.StartEditMessage -> MessageActionStateReducer.startEdit(
                            this,
                            msg.messageId,
                            msg.message
                        )
                        is Msg.CancelEditMessage -> MessageActionStateReducer.cancelEdit(this)
                        is Msg.StartReplyMessage -> MessageActionStateReducer.startReply(this, msg.message)
                        is Msg.CancelReplyMessage -> MessageActionStateReducer.cancelReply(this)
                        is Msg.SetUser -> copy(currentUser = msg.user)
                    }
                }
            ) {}

    private suspend fun CoroutineBootstrapperScope<Action>.getChat(id: String, userId: String) =
        withContext(Dispatchers.IO) {
            val chat = chatRepository.getChat(id)
            withContext(Dispatchers.Main) {
                dispatch(Action.GetChat(chat.toUi(userId)))
            }
            chatRepository.getChatMessages(id).collect { messages ->
                val uiMessages = messages.map { it.toUi(userId) }
                withContext(Dispatchers.Main) {
                    dispatch(Action.GetMessages(uiMessages))
                }
            }
        }

    @OptIn(ExperimentalUuidApi::class)
    private fun CoroutineExecutorScope<State, Msg, Nothing, Nothing>.sendMessage() {
        if (state().currentMessage.isBlank()) return

        val messageText = state().currentMessage.trim()
        val editingMessageId = state().editingMessageId
        val reply = state().replyMetadata()
        val currentUserId = state().currentUser.id
        dispatch(Msg.ClearMessage)

        if (editingMessageId != null) {
            launch(Dispatchers.IO) {
                chatRepository.updateMessage(chatId, editingMessageId, messageText)
            }
            return
        }

        val message = MessageDraftFactory.textMessage(
            id = Uuid.random().toString(),
            senderId = currentUserId,
            text = messageText,
            reply = reply
        )
        dispatch(Msg.AddPendingMessage(message.copy(isPending = true).toUi(currentUserId)))

        launch(Dispatchers.IO) {
            runCatching {
                chatRepository.sendMessage(chatId, message)
            }.onFailure {
                withContext(Dispatchers.Main) {
                    dispatch(Msg.RemovePendingMessage(message.id))
                }
                Log.d("ChatStore", it.message.toString())
            }
        }
    }

    private fun CoroutineExecutorScope<State, Nothing, Nothing, Nothing>.readMessage(ids: List<String>) {
        if (state().chat == null) return
        launch(Dispatchers.IO) {
            chatRepository.readMessage(chatId, ids)
        }
    }

    private fun CoroutineExecutorScope<State, Msg, Nothing, Nothing>.deleteMessage(messageId: String) {
        if (messageId == state().editingMessageId) dispatch(Msg.CancelEditMessage)
        if (messageId == state().replyingToMessage?.id) dispatch(Msg.CancelReplyMessage)
        launch(Dispatchers.IO) {
            runCatching { chatRepository.deleteMessage(chatId, messageId) }
        }
    }

    private fun State.withRemoteMessages(messages: List<UiMessage>): State {
        val remoteIds = messages.mapTo(mutableSetOf()) { it.id }
        val nextPendingMessages = pendingMessages.filterNot { it.id in remoteIds }
        return copy(
            remoteMessages = messages,
            pendingMessages = nextPendingMessages,
            messages = (messages + nextPendingMessages).toMessageListItems()
        )
    }

    private fun State.withPendingMessage(message: UiMessage): State {
        val nextPendingMessages = pendingMessages
            .filterNot { it.id == message.id } + message
        return copy(
            pendingMessages = nextPendingMessages,
            messages = (remoteMessages + nextPendingMessages).toMessageListItems()
        )
    }

    private fun State.withoutPendingMessage(messageId: String): State {
        val nextPendingMessages = pendingMessages.filterNot { it.id == messageId }
        return copy(
            pendingMessages = nextPendingMessages,
            messages = (remoteMessages + nextPendingMessages).toMessageListItems()
        )
    }

    private fun List<UiMessage>.toMessageListItems() =
        sortedByDescending { it.sentAt }
            .groupBy { it.sentAt.toDateString() }
            .flatMap { (date, messages) ->
                messages.map { MessageListItem.MessageItem(it) } + listOf(MessageListItem.DateItem(date))
            }

    @OptIn(ExperimentalUuidApi::class)
    private fun State.pendingMediaMessage(
        content: String,
        isPhoto: Boolean = false,
        isVoice: Boolean = false,
        durationMillis: Long = 0L,
        sizeBytes: Long = 0L
    ): UiMessage {
        val reply = replyMetadata()
        val message = MessageDraftFactory.mediaMessage(
            id = Uuid.random().toString(),
            senderId = currentUser.id,
            content = content,
            isPhoto = isPhoto,
            isVoice = isVoice,
            durationMillis = durationMillis,
            sizeBytes = sizeBytes,
            reply = reply
        )
        return message.toUi(currentUser.id)
    }

    private fun State.replyMetadata(): ReplyMetadata {
        val message = replyingToMessage ?: return ReplyMetadata()
        return ReplyMetadata(
            messageId = message.id,
            sender = if (message.isCurrentUserMessage) {
                currentUser.username.ifBlank { "Вы" }
            } else {
                chat?.user?.username?.ifBlank { "Сообщение" } ?: "Сообщение"
            },
            text = message.replyPreviewText().take(140)
        )
    }

    private fun UiMessage.replyPreviewText(): String =
        when {
            isPhoto -> "Фото"
            isVoice -> "Голосовое сообщение"
            else -> message
        }

}
