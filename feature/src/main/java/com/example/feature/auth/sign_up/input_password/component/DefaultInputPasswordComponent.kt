package com.example.feature.auth.sign_up.input_password.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.example.data.auth.api.AuthRepository
import com.example.data.user.api.UserDataStoreRepository
import com.example.data.user.api.UserRepository
import com.example.data.user.api.model.UserData
import com.example.feature.auth.sign_up.input_password.store.InputPasswordStore
import com.example.feature.auth.sign_up.input_password.store.InputPasswordStoreFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

class DefaultInputPasswordComponent(
    componentContext: ComponentContext,
    private val storeFactory: StoreFactory,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val userDataStoreRepository: UserDataStoreRepository,
    private val currentUserData: UserData,
    private val navigateToHome: () -> Unit,
    private val navigateBack: () -> Unit
) : ComponentContext by componentContext, InputPasswordComponent {

    private val store = instanceKeeper.getStore {
        InputPasswordStoreFactory(
            factory = storeFactory,
            authRepository = authRepository,
            userRepository = userRepository,
            userDataStoreRepository = userDataStoreRepository,
            currentUserData = currentUserData
        ).create()
    }

    private val scope = CoroutineScope(Dispatchers.Main)

    @OptIn(ExperimentalCoroutinesApi::class)
    override val state = store.stateFlow

    override fun onIntent(intent: InputPasswordStore.Intent) {
        store.accept(intent)
    }

    init {
        scope.launch {
            store.labels.collect { label ->
                when (label) {
                    is InputPasswordStore.Label.NavigateToHome -> navigateToHome()
                    is InputPasswordStore.Label.NavigateBack -> navigateBack()
                }
            }
        }
    }
}