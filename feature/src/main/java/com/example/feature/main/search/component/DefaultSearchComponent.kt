package com.example.feature.main.search.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.example.data.chat.api.ChatRepository
import com.example.data.user.api.UserDataStoreRepository
import com.example.data.user.api.UserRepository
import com.example.data.user.api.model.UserData
import com.example.feature.main.search.store.SearchStore
import com.example.feature.main.search.store.SearchStoreFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class DefaultSearchComponent(
    componentContext: ComponentContext,
    private val storeFactory: StoreFactory,
    private val userRepository: UserRepository,
    private val userDataFlow: Flow<UserData>,
    private val chatRepository: ChatRepository,
    private val navigateToChat: (String) -> Unit,
    private val navigateBack: () -> Unit
) : ComponentContext by componentContext, SearchComponent {

    private val store = instanceKeeper.getStore {
        SearchStoreFactory(
            factory = storeFactory,
            userRepository = userRepository,
            userDataFlow = userDataFlow,
            chatRepository = chatRepository
        ).create()
    }

    private val scope = CoroutineScope(Dispatchers.Main)

    @OptIn(ExperimentalCoroutinesApi::class)
    override val state = store.stateFlow

    override fun onIntent(intent: SearchStore.Intent) {
        store.accept(intent)
    }

    init {
        scope.launch {
            store.labels.collect { label ->
                when (label) {
                    is SearchStore.Label.OpenChat -> navigateToChat(label.chatId)
                    is SearchStore.Label.NavigateBack -> navigateBack()
                }
            }
        }
    }
}