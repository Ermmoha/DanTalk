package com.example.feature.main.people.store

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapperScope
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutorScope
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import com.example.background.service.ImageLoadServiceStarter
import com.example.core.ui.model.UiUserData
import com.example.data.chat.api.ChatRepository
import com.example.data.media.api.MediaRepository
import com.example.data.storage.api.StorageRepository
import com.example.data.user.api.UserRepository
import com.example.data.user.api.model.UserData
import com.example.feature.main.people.store.PeopleStore.Intent
import com.example.feature.main.people.store.PeopleStore.Label
import com.example.feature.main.people.store.PeopleStore.State
import com.example.feature.mapper.toUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PeopleStoreFactory(
    private val factory: StoreFactory,
    private val userRepository: UserRepository,
    private val userDataFlow: Flow<UserData>,
    private val chatRepository: ChatRepository,
) {
    sealed interface Action {
        class SetUserId(val userId: String) : Action
    }

    sealed interface Msg {
        class OnQueryChange(val query: String) : Msg
        class UpdateUsers(val usersByQuery: List<UiUserData>) : Msg
        class UpdateLoading(val isLoading: Boolean) : Msg
        class SetUserId(val userId: String) : Msg
    }

    fun create(): PeopleStore =
        object : PeopleStore,
            Store<Intent, State, Label> by factory.create<Intent, Action, Msg, State, Label>(
                name = "PeopleStore",
                initialState = State(),
                bootstrapper = coroutineBootstrapper { getCurrentUserId() },
                executorFactory = coroutineExecutorFactory {
                    onAction<Action.SetUserId> { dispatch(Msg.SetUserId(it.userId)) }
                    onIntent<Intent.OnQueryChange> { getUsersByQuery(it.query) }
                    onIntent<Intent.OpenChat> { getChatIdByUserId(it.userId) }
                    onIntent<Intent.DownloadImage> { ImageLoadServiceStarter.download(it.context, it.url) }
                    onIntent<Intent.NavigateBack> { publish(Label.NavigateBack) }
                },
                reducer = { msg ->
                    when (msg) {
                        is Msg.OnQueryChange -> copy(query = msg.query)
                        is Msg.UpdateUsers -> copy(usersByQuery = msg.usersByQuery)
                        is Msg.UpdateLoading -> copy(isLoading = msg.isLoading)
                        is Msg.SetUserId -> copy(currentUserId = msg.userId)
                    }
                }
            ) {}

    private fun CoroutineBootstrapperScope<Action>.getCurrentUserId() {
        launch {
            userDataFlow.collect { user ->
                dispatch(Action.SetUserId(user.id))
            }
        }
    }

    private fun CoroutineExecutorScope<State, Msg, Nothing, Nothing>.getUsersByQuery(query: String) {
        dispatch(Msg.OnQueryChange(query))
        dispatch(Msg.UpdateLoading(true))
        val updQuery = query.trim()
        val currentUserId = state().currentUserId
        launch(Dispatchers.IO) {
            val users = userRepository.getUsersByQuery(updQuery).filter { it.id != currentUserId }
            withContext(Dispatchers.Main) {
                dispatch(Msg.UpdateUsers(users.toUi()))
                dispatch(Msg.UpdateLoading(false))
            }
        }
    }

    private fun CoroutineExecutorScope<State, Msg, Nothing, Label>.getChatIdByUserId(userId: String) {
        launch {
            val userIds = listOf(state().currentUserId, userId)
            try {
                withContext(Dispatchers.IO) {
                    chatRepository.getChatByUserIds(userIds)
                }.let { publish(Label.OpenChat(it.id)) }
            } catch (e: Exception) {
                withContext(Dispatchers.IO) {
                    chatRepository.createChat(userIds)
                }.let { publish(Label.OpenChat(it)) }
            }
        }
    }
}