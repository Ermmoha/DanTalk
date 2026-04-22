package com.example.feature.main.chat.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.example.data.chat.api.ChatRepository
import com.example.data.user.api.model.UserData
import com.example.feature.main.chat.store.ChatStore
import com.example.feature.main.chat.store.ChatStoreFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class DefaultChatComponent(
    componentContext: ComponentContext,
    private val storeFactory: StoreFactory,
    private val chatRepository: ChatRepository,
    private val userDataFlow: Flow<UserData>,
    private val chatId: String,
    private val navigateBack: () -> Unit
) : ChatComponent, ComponentContext by componentContext {

    private val store = instanceKeeper.getStore {
        ChatStoreFactory(
            factory = storeFactory,
            chatRepository = chatRepository,
            userDataFlow = userDataFlow,
            chatId = chatId
        ).create()
    }

    private val scope = CoroutineScope(Dispatchers.Main)

    @OptIn(ExperimentalCoroutinesApi::class)
    override val state = store.stateFlow

    override fun onIntent(intent: ChatStore.Intent) {
        store.accept(intent)
    }

    init {
        scope.launch {
            store.labels.collect { label ->
                when (label) {
                    is ChatStore.Label.NavigateBack -> navigateBack()
                }
            }
        }
    }
}
