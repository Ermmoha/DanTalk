package com.example.feature.auth.sign_in.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.example.feature.auth.sign_in.store.SignInStore
import com.example.feature.auth.sign_in.store.SignInStoreFactory
import com.example.data.auth.api.AuthRepository
import com.example.data.user.api.UserDataStoreRepository
import com.example.data.user.api.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

class DefaultSignInComponent(
    componentContext: ComponentContext,
    private val storeFactory: StoreFactory,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val userDataStoreRepository: UserDataStoreRepository,
    private val navigateToSignUp: () -> Unit,
    private val navigateToHome: () -> Unit,
) : ComponentContext by componentContext, SignInComponent {

    private val store = instanceKeeper.getStore {
        SignInStoreFactory(
            factory = storeFactory,
            authRepository = authRepository,
            userRepository = userRepository,
            userDataStoreRepository = userDataStoreRepository
        ).create()
    }

    private val scope = CoroutineScope(Dispatchers.Main)

    @OptIn(ExperimentalCoroutinesApi::class)
    override val state = store.stateFlow

    override fun onIntent(intent: SignInStore.Intent) {
        store.accept(intent)
    }

    init {
        scope.launch {
            store.labels.collect { label ->
                when (label) {
                    SignInStore.Label.NavigateToSignUp -> navigateToSignUp()
                    SignInStore.Label.NavigateToHome -> navigateToHome()
                }
            }
        }
    }
}