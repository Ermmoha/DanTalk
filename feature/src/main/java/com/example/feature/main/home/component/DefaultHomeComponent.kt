package com.example.feature.main.home.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.example.data.app_theme.api.AppThemeDataStoreRepository
import com.example.data.chat.api.ChatRepository
import com.example.data.settings.api.AppSettingsRepository
import com.example.data.user.api.model.UserData
import com.example.feature.main.home.store.HomeStore
import com.example.feature.main.home.store.HomeStoreFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class DefaultHomeComponent(
    componentContext: ComponentContext,
    private val storeFactory: StoreFactory,
    private val chatRepository: ChatRepository,
    private val appThemeDataStoreRepository: AppThemeDataStoreRepository,
    private val appSettingsRepository: AppSettingsRepository,
    private val userDataFlow: Flow<UserData>,
    private val clearUserData: () -> Unit,
    private val navigateToSearch: () -> Unit,
    private val navigateToProfile: () -> Unit,
    private val navigateToPeople: () -> Unit,
    private val navigateToSettings: () -> Unit,
    private val navigateToHelp: () -> Unit,
    private val navigateToAuth: () -> Unit,
    private val navigateToChat: (id: String) -> Unit
) : ComponentContext by componentContext, HomeComponent {

    private val store = instanceKeeper.getStore {
        HomeStoreFactory(
            factory = storeFactory,
            chatRepository = chatRepository,
            appThemeDataStoreRepository = appThemeDataStoreRepository,
            appSettingsRepository = appSettingsRepository,
            userDataFlow = userDataFlow,
            clearUserData = clearUserData
        ).create()
    }

    private val scope = CoroutineScope(Dispatchers.Main)

    @OptIn(ExperimentalCoroutinesApi::class)
    override val state = store.stateFlow

    override fun onIntent(intent: HomeStore.Intent) {
        store.accept(intent)
    }

    init {
        scope.launch {
            store.labels.collect { label ->
                when (label) {
                    is HomeStore.Label.NavigateToSearch -> navigateToSearch()
                    is HomeStore.Label.NavigateToProfile -> navigateToProfile()
                    is HomeStore.Label.NavigateToPeople -> navigateToPeople()
                    is HomeStore.Label.NavigateToSettings -> navigateToSettings()
                    is HomeStore.Label.NavigateToHelp -> navigateToHelp()
                    is HomeStore.Label.NavigateToAuth -> navigateToAuth()
                    is HomeStore.Label.NavigateToChat -> navigateToChat(label.id)
                }
            }
        }
    }
}
