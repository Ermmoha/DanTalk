package com.example.feature.auth.sign_up.input_name.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.example.data.user.api.model.UserData
import com.example.feature.auth.sign_up.input_name.store.InputNameStore
import com.example.feature.auth.sign_up.input_name.store.InputNameStoreFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

class DefaultInputNameComponent(
    componentContext: ComponentContext,
    private val storeFactory: StoreFactory,
    private val navigateToInputPassword: (UserData) -> Unit,
    private val navigateBack: () -> Unit,
    private val currentUserData: UserData,
) : ComponentContext by componentContext, InputNameComponent {

    private val store = instanceKeeper.getStore {
        InputNameStoreFactory(
            factory = storeFactory,
            currentUserData = currentUserData,
        ).create()
    }

    private val scope = CoroutineScope(Dispatchers.Main)

    @OptIn(ExperimentalCoroutinesApi::class)
    override val state = store.stateFlow

    override fun onIntent(intent: InputNameStore.Intent) {
        store.accept(intent)
    }

    init {
        scope.launch {
            store.labels.collect { label ->
                when (label) {
                    is InputNameStore.Label.NavigateNext -> navigateToInputPassword(label.userData)
                    is InputNameStore.Label.NavigateBack -> navigateBack()
                }
            }
        }
    }
}