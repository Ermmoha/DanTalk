package com.example.feature.main.profile.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.example.dantalk.features.main.profile.component.ProfileComponent
import com.example.data.storage.api.StorageRepository
import com.example.data.user.api.UserDataStoreRepository
import com.example.data.user.api.UserRepository
import com.example.feature.main.profile.store.ProfileStore
import com.example.feature.main.profile.store.ProfileStoreFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

class DefaultProfileComponent(
    componentContext: ComponentContext,
    private val storeFactory: StoreFactory,
    private val userRepository: UserRepository,
    private val userDataStoreRepository: UserDataStoreRepository,
    private val navigateBack: () -> Unit
) : ComponentContext by componentContext, ProfileComponent {

    private val store = instanceKeeper.getStore {
        ProfileStoreFactory(
            factory = storeFactory,
            userRepository = userRepository,
            userDataStoreRepository = userDataStoreRepository,
        ).create()
    }

    private val scope = CoroutineScope(Dispatchers.Main)

    @OptIn(ExperimentalCoroutinesApi::class)
    override val state = store.stateFlow

    override fun onIntent(intent: ProfileStore.Intent) {
        store.accept(intent)
    }

    init {
        scope.launch {
            store.labels.collect { label ->
                when (label) {
                    is ProfileStore.Label.NavigateBack -> navigateBack()
                }
            }
        }
    }
}