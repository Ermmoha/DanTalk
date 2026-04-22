package com.example.feature.auth.sign_up.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DelicateDecomposeApi
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.example.data.auth.api.AuthRepository
import com.example.data.user.api.UserDataStoreRepository
import com.example.data.user.api.UserRepository
import com.example.data.user.api.model.UserData
import com.example.feature.auth.sign_up.input_email.component.DefaultInputEmailComponent
import com.example.feature.auth.sign_up.input_name.component.DefaultInputNameComponent
import com.example.feature.auth.sign_up.input_password.component.DefaultInputPasswordComponent
import kotlinx.serialization.Serializable

class DefaultSignUpComponent(
    componentContext: ComponentContext,
    private val storeFactory: StoreFactory,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val userDataStoreRepository: UserDataStoreRepository,
    private val navigateToHome: () -> Unit,
    private val navigateToSignIn: () -> Unit,
) : ComponentContext by componentContext, SignUpComponent {

    private val navigation = StackNavigation<Config>()

    override val stack = childStack(
        source = navigation,
        initialConfiguration = Config.InputEmail,
        serializer = Config.serializer(),
        handleBackButton = true,
        childFactory = ::child
    )

    private fun child(
        config: Config,
        componentContext: ComponentContext,
    ): SignUpComponent.Child =
        when (config) {
            is Config.InputEmail -> SignUpComponent.Child.InputEmail(
                inputEmailComponent(componentContext)
            )

            is Config.InputName -> SignUpComponent.Child.InputName(
                inputNameComponent(
                    config = Config.InputName(config.userData),
                    componentContext = componentContext
                )
            )

            is Config.InputPassword -> SignUpComponent.Child.InputPassword(
                inputPasswordComponent(
                    config = Config.InputPassword(config.userData),
                    componentContext = componentContext
                )
            )
        }

    @OptIn(DelicateDecomposeApi::class)
    private fun inputEmailComponent(componentContext: ComponentContext) =
        DefaultInputEmailComponent(
            componentContext = componentContext,
            storeFactory = storeFactory,
            userRepository = userRepository,
            navigateNext = { user -> navigation.push(Config.InputName(user)) },
            navigateToSignIn = navigateToSignIn
        )

    @OptIn(DelicateDecomposeApi::class)
    private fun inputNameComponent(config: Config.InputName, componentContext: ComponentContext) =
        DefaultInputNameComponent(
            componentContext = componentContext,
            storeFactory = storeFactory,
            navigateToInputPassword = { user -> navigation.push(Config.InputPassword(user)) },
            navigateBack = { navigation.pop() },
            currentUserData = config.userData
        )

    private fun inputPasswordComponent(
        config: Config.InputPassword,
        componentContext: ComponentContext,
    ) =
        DefaultInputPasswordComponent(
            componentContext = componentContext,
            storeFactory = storeFactory,
            authRepository = authRepository,
            userRepository = userRepository,
            userDataStoreRepository = userDataStoreRepository,
            navigateToHome = navigateToHome,
            navigateBack = { navigation.pop() },
            currentUserData = config.userData
        )

    @Serializable
    sealed interface Config {
        @Serializable
        data object InputEmail : Config

        @Serializable
        class InputName(val userData: UserData) : Config

        @Serializable
        class InputPassword(val userData: UserData) : Config
    }
}