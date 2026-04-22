package com.example.feature.main.home.store

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapperScope
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutorScope
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import com.example.core.design.AppTheme
import com.example.core.ui.model.UiChat
import com.example.core.ui.model.UiUserData
import com.example.data.app_theme.api.AppThemeDataStoreRepository
import com.example.data.chat.api.ChatRepository
import com.example.data.chat.api.model.Chat
import com.example.data.settings.api.AppSettingsRepository
import com.example.data.user.api.model.UserData
import com.example.feature.main.home.store.HomeStore.Intent
import com.example.feature.main.home.store.HomeStore.Label
import com.example.feature.main.home.store.HomeStore.State
import com.example.feature.mapper.toUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeStoreFactory(
    private val factory: StoreFactory,
    private val chatRepository: ChatRepository,
    private val appThemeDataStoreRepository: AppThemeDataStoreRepository,
    private val appSettingsRepository: AppSettingsRepository,
    private val userDataFlow: Flow<UserData>,
    private val clearUserData: () -> Unit,
) {
    private sealed interface Action {
        class GetChats(val chats: List<UiChat>) : Action
        class SetUser(val user: UiUserData) : Action
        class UpdateLoading(val isLoading: Boolean) : Action
    }

    private sealed interface Msg {
        class GetChats(val chats: List<UiChat>) : Msg
        class SetUser(val user: UiUserData) : Msg
        class UpdateLoading(val isLoading: Boolean) : Msg
    }

    fun create(): HomeStore =
        object : HomeStore,
            Store<Intent, State, Label> by factory.create<Intent, Action, Msg, State, Label>(
                name = "HomeStore",
                initialState = State(),
                bootstrapper = coroutineBootstrapper { getData() },
                executorFactory = coroutineExecutorFactory {
                    onAction<Action.GetChats> { dispatch(Msg.GetChats(it.chats)) }
                    onAction<Action.SetUser> { dispatch(Msg.SetUser(it.user)) }
                    onAction<Action.UpdateLoading> { dispatch(Msg.UpdateLoading(it.isLoading)) }
                    onIntent<Intent.NavigateToSearch> { publish(Label.NavigateToSearch) }
                    onIntent<Intent.NavigateToProfile> { publish(Label.NavigateToProfile) }
                    onIntent<Intent.NavigateToPeople> { publish(Label.NavigateToPeople) }
                    onIntent<Intent.NavigateToSettings> { publish(Label.NavigateToSettings) }
                    onIntent<Intent.NavigateToHelp> { publish(Label.NavigateToHelp) }
                    onIntent<Intent.OpenChat> { publish(Label.NavigateToChat(it.id)) }
                    onIntent<Intent.DeleteChat> { launch { deleteChat(it.id) } }
                    onIntent<Intent.TogglePinChat> { launch { togglePinChat(it.id) } }
                    onIntent<Intent.ChangeAppTheme> { launch { changeAppTheme(it.theme) } }
                    onIntent<Intent.SignOut> { signOut() }
                },
                reducer = { msg ->
                    when (msg) {
                        is Msg.GetChats -> copy(chats = msg.chats)
                        is Msg.SetUser -> copy(user = msg.user)
                        is Msg.UpdateLoading -> copy(isLoading = msg.isLoading)
                    }
                }
            ) {}

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun CoroutineBootstrapperScope<Action>.getData() {
        dispatch(Action.UpdateLoading(true))
        var first = true
        launch {
            userDataFlow
                .map { it.toUi() }
                .flatMapLatest { user ->
                    dispatch(Action.SetUser(user))
                    combine(
                        getChatsFlow(user.id),
                        appSettingsRepository.pinnedChatsFlow
                    ) { chats, pinnedChats ->
                        chats
                            .map { it.copy(isPinned = pinnedChats.contains(it.id)) }
                            .sortedWith(
                                compareByDescending<UiChat> { it.isPinned }
                                    .thenByDescending { it.lastMessage?.sentAt ?: 0L }
                            )
                    }
                }
                .onEach {
                    if (first) {
                        first = false
                        dispatch(Action.UpdateLoading(false))
                    }
                }
                .collect { chats ->
                    dispatch(Action.GetChats(chats))
                }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun getChatsFlow(userId: String): Flow<List<UiChat>> =
        chatRepository.getChats(userId)
            .flatMapLatest { chats ->
                if (chats.isEmpty())
                    flowOf(emptyList())
                else
                    combine(
                        chats.map { chat ->
                            chatRepository.getChatMessages(chat.id)
                                .map { messages ->
                                    Chat(
                                        id = chat.id,
                                        users = chat.users
                                    ).toUi(userId, messages.toUi(userId))
                                }
                        }
                    ) { it.toList() }
            }
            .flowOn(Dispatchers.IO)

    private suspend fun deleteChat(id: String) = withContext(Dispatchers.IO) {
        chatRepository.deleteChat(id)
        appSettingsRepository.setChatPinned(id, false)
    }

    private suspend fun togglePinChat(id: String) = withContext(Dispatchers.IO) {
        appSettingsRepository.togglePinnedChat(id)
    }

    private suspend fun changeAppTheme(theme: AppTheme) = withContext(Dispatchers.IO) {
        appThemeDataStoreRepository.setTheme(theme)
    }

    private fun CoroutineExecutorScope<State, Nothing, Nothing, Label>.signOut() {
        clearUserData()
        publish(Label.NavigateToAuth)
    }
}
