package com.example.feature.main.settings.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.example.data.app_theme.api.AppThemeDataStoreRepository
import com.example.data.settings.api.AppSettingsRepository
import com.example.data.user.api.model.UserData
import com.example.feature.main.settings.store.SettingsStore
import com.example.feature.main.settings.store.SettingsStoreFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class DefaultSettingsComponent(
    componentContext: ComponentContext,
    private val storeFactory: StoreFactory,
    private val userDataFlow: Flow<UserData>,
    private val appThemeDataStoreRepository: AppThemeDataStoreRepository,
    private val appSettingsRepository: AppSettingsRepository,
    private val navigateToHelp: () -> Unit,
    private val navigateBack: () -> Unit
) : ComponentContext by componentContext, SettingsComponent {

    private val store = instanceKeeper.getStore {
        SettingsStoreFactory(
            factory = storeFactory,
            userDataFlow = userDataFlow,
            appThemeDataStoreRepository = appThemeDataStoreRepository,
            appSettingsRepository = appSettingsRepository
        ).create()
    }

    private val scope = CoroutineScope(Dispatchers.Main)

    @OptIn(ExperimentalCoroutinesApi::class)
    override val state = store.stateFlow

    override fun onIntent(intent: SettingsStore.Intent) {
        store.accept(intent)
    }

    init {
        scope.launch {
            store.labels.collect { label ->
                when (label) {
                    is SettingsStore.Label.NavigateToHelp -> navigateToHelp()
                    is SettingsStore.Label.NavigateBack -> navigateBack()
                }
            }
        }
    }
}
