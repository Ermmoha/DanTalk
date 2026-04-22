package com.example.feature.auth.sign_up.input_email.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.example.data.user.api.UserRepository
import com.example.data.user.api.model.UserData
import com.example.feature.auth.sign_up.input_email.store.InputEmailStore
import com.example.feature.auth.sign_up.input_email.store.InputEmailStoreFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

class DefaultInputEmailComponent(
    componentContext: ComponentContext,
    private val storeFactory: StoreFactory,
    private val userRepository: UserRepository,
    private val navigateNext: (UserData) -> Unit,
    private val navigateToSignIn: () -> Unit,
) : ComponentContext by componentContext, InputEmailComponent {

    private val store = instanceKeeper.getStore {
        InputEmailStoreFactory(
            factory = storeFactory,
            userRepository = userRepository
        ).create()
    }

    private val scope = CoroutineScope(Dispatchers.Main)

    @OptIn(ExperimentalCoroutinesApi::class)
    override val state = store.stateFlow

    override fun onIntent(intent: InputEmailStore.Intent) {
        store.accept(intent)
    }

    init {
        scope.launch {
            store.labels.collect { label ->
                when (label) {
                    is InputEmailStore.Label.NavigateNext -> navigateNext(label.userData)
                    is InputEmailStore.Label.NavigateToSignIn -> navigateToSignIn()
                }
            }
        }
    }
}