package com.example.feature.main.search.store

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutorScope
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import com.example.core.ui.model.UiChat
import com.example.data.chat.api.ChatRepository
import com.example.data.user.api.UserRepository
import com.example.data.user.api.model.UserData
import com.example.feature.main.search.store.SearchStore.Intent
import com.example.feature.main.search.store.SearchStore.Label
import com.example.feature.main.search.store.SearchStore.State
import com.example.feature.mapper.toUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class SearchStoreFactory(
    private val factory: StoreFactory,
    private val userRepository: UserRepository,
    private val userDataFlow: Flow<UserData>,
    private val chatRepository: ChatRepository,
) {
    sealed interface Msg {
        class OnQueryChange(val query: String) : Msg
        class UpdateChats(val chatsByQuery: List<UiChat>) : Msg
        class UpdateLoading(val isLoading: Boolean) : Msg
    }

    fun create(): SearchStore =
        object : SearchStore,
            Store<Intent, State, Label> by factory.create<Intent, Nothing, Msg, State, Label>(
                name = "SearchStore",
                initialState = State(),
                executorFactory = coroutineExecutorFactory {
                    onIntent<Intent.OnQueryChange> { onQueryChange(it.query) }
                    onIntent<Intent.OpenChat> { publish(Label.OpenChat(it.chatId)) }
                    onIntent<Intent.NavigateBack> { publish(Label.NavigateBack) }
                },
                reducer = { msg ->
                    when (msg) {
                        is Msg.OnQueryChange -> copy(query = msg.query)
                        is Msg.UpdateChats -> copy(chatsByQuery = msg.chatsByQuery)
                        is Msg.UpdateLoading -> copy(isLoading = msg.isLoading)
                    }
                }
            ) {}

    private var searchJob: Job? = null

    private fun CoroutineExecutorScope<State, Msg, Nothing, Nothing>.onQueryChange(query: String) {
        dispatch(Msg.OnQueryChange(query))
        searchJob?.cancel()
        var first = true

        searchJob = launch {
            val formattedQuery = query.trim()

            if (formattedQuery.isEmpty()) {
                dispatch(Msg.UpdateChats(emptyList()))
                dispatch(Msg.UpdateLoading(false))
                return@launch
            }

            dispatch(Msg.UpdateChats(emptyList()))
            dispatch(Msg.UpdateLoading(true))

            getChatsByQuery(formattedQuery)
                .onEach {
                    if (first) {
                        first = false
                        dispatch(Msg.UpdateLoading(false))
                    }
                }
                .collect { chats ->
                    dispatch(Msg.UpdateChats(chats))
                }
        }
    }

    private fun getChatsByQuery(query: String): Flow<List<UiChat>> = flow {
        try {
            val currentUserId = userDataFlow.first().id
            val users = userRepository.getUsersByQuery(query).toUi()
            if (users.isEmpty()) emit(emptyList())

            val chatFlows = users.map { user ->
                val chat = chatRepository.getChatByUserIds(listOf(currentUserId, user.id))
                chatRepository.getChatMessages(chat.id).map { messages ->
                    chat.toUi(currentUserId, messages.toUi(currentUserId))
                }
            }

            emitAll(combine(chatFlows) { it.toList() })
        } catch (e: Exception) {
            emit(emptyList())
        }
    }.flowOn(Dispatchers.IO)
}