package com.example.feature.root.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.replaceAll
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.example.core.design.AppTheme
import com.example.data.app_theme.api.AppThemeDataStoreRepository
import com.example.data.auth.api.AuthRepository
import com.example.data.chat.api.ChatRepository
import com.example.data.settings.api.AppSettingsRepository
import com.example.data.user.api.UserDataStoreRepository
import com.example.data.user.api.UserRepository
import com.example.feature.auth.component.DefaultAuthComponent
import com.example.feature.main.component.DefaultMainComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable

class DefaultRootComponent(
    componentContext: ComponentContext,
    private val storeFactory: StoreFactory,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository,
    private val userDataStoreRepository: UserDataStoreRepository,
    private val appThemeDataStoreRepository: AppThemeDataStoreRepository,
    private val appSettingsRepository: AppSettingsRepository
) : ComponentContext by componentContext, RootComponent {

    private val navigation = StackNavigation<Config>()
    private val scope = CoroutineScope(Dispatchers.IO)

    override val themeState: StateFlow<AppTheme> = appThemeDataStoreRepository.themeFlow.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppTheme.SYSTEM
    )

    private suspend fun getInitialConfiguration(): Config {
        val user = userDataStoreRepository.getUserData.first()
        return if (user.id.isNotEmpty()) Config.Main else Config.Auth
    }

    private fun clearUserData() {
        scope.launch {
            authRepository.signOut()
            userDataStoreRepository.clearUserData()
        }
    }

    override val stack = childStack(
        source = navigation,
        initialConfiguration = runBlocking(Dispatchers.IO) { getInitialConfiguration() },
        serializer = Config.serializer(),
        handleBackButton = false,
        childFactory = ::child
    )

    private fun child(
        config: Config,
        componentContext: ComponentContext,
    ): RootComponent.Child =
        when (config) {
            is Config.Auth -> RootComponent.Child.Auth(authComponent(componentContext))
            is Config.Main -> RootComponent.Child.Main(mainComponent(componentContext))
        }

    private fun authComponent(componentContext: ComponentContext) =
        DefaultAuthComponent(
            componentContext = componentContext,
            storeFactory = storeFactory,
            authRepository = authRepository,
            userRepository = userRepository,
            userDataStoreRepository = userDataStoreRepository,
            navigateToHome = { navigation.replaceAll(Config.Main) }
        )

    private fun mainComponent(componentContext: ComponentContext) =
        DefaultMainComponent(
            componentContext = componentContext,
            storeFactory = storeFactory,
            userRepository = userRepository,
            chatRepository = chatRepository,
            userDataStoreRepository = userDataStoreRepository,
            appThemeDataStoreRepository = appThemeDataStoreRepository,
            appSettingsRepository = appSettingsRepository,
            clearUserData = ::clearUserData,
            navigateToAuth = { navigation.replaceAll(Config.Auth) }
        )

    @Serializable
    sealed interface Config {
        @Serializable
        data object Auth : Config

        @Serializable
        data object Main : Config
    }
}
