package com.example.feature.auth.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DelicateDecomposeApi
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.example.feature.auth.sign_in.component.DefaultSignInComponent
import com.example.data.auth.api.AuthRepository
import com.example.data.user.api.UserDataStoreRepository
import com.example.data.user.api.UserRepository
import com.example.feature.auth.sign_up.component.DefaultSignUpComponent
import kotlinx.serialization.Serializable

class DefaultAuthComponent(
    componentContext: ComponentContext,
    private val storeFactory: StoreFactory,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val userDataStoreRepository: UserDataStoreRepository,
    private val navigateToHome: () -> Unit,
) : ComponentContext by componentContext, AuthComponent {

    private val navigation = StackNavigation<Config>()

    override val stack = childStack(
        source = navigation,
        initialConfiguration = Config.SignIn,
        serializer = Config.serializer(),
        handleBackButton = true,
        childFactory = ::child
    )

    private fun child(
        config: Config,
        componentContext: ComponentContext,
    ): AuthComponent.Child =
        when (config) {
            is Config.SignIn -> AuthComponent.Child.SignIn(signInComponent(componentContext))
            is Config.SignUp -> AuthComponent.Child.SignUp(signUpComponent(componentContext))
        }

    @OptIn(DelicateDecomposeApi::class)
    private fun signInComponent(componentContext: ComponentContext) =
        DefaultSignInComponent(
            componentContext = componentContext,
            storeFactory = storeFactory,
            authRepository = authRepository,
            userRepository = userRepository,
            userDataStoreRepository = userDataStoreRepository,
            navigateToSignUp = { navigation.push(Config.SignUp) },
            navigateToHome = navigateToHome
        )

    private fun signUpComponent(componentContext: ComponentContext) =
        DefaultSignUpComponent(
            componentContext = componentContext,
            storeFactory = storeFactory,
            authRepository = authRepository,
            userRepository = userRepository,
            userDataStoreRepository = userDataStoreRepository,
            navigateToHome = navigateToHome,
            navigateToSignIn = { navigation.pop() }
        )

    @Serializable
    sealed interface Config {
        @Serializable
        data object SignIn : Config

        @Serializable
        data object SignUp : Config
    }
}