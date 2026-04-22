package com.example.feature.main.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DelicateDecomposeApi
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.example.data.app_theme.api.AppThemeDataStoreRepository
import com.example.data.chat.api.ChatRepository
import com.example.data.settings.api.AppSettingsRepository
import com.example.data.user.api.UserDataStoreRepository
import com.example.data.user.api.UserRepository
import com.example.feature.main.chat.component.DefaultChatComponent
import com.example.feature.main.help.component.DefaultHelpComponent
import com.example.feature.main.home.component.DefaultHomeComponent
import com.example.feature.main.people.component.DefaultPeopleComponent
import com.example.feature.main.profile.component.DefaultProfileComponent
import com.example.feature.main.search.component.DefaultSearchComponent
import com.example.feature.main.settings.component.DefaultSettingsComponent
import kotlinx.serialization.Serializable

@OptIn(DelicateDecomposeApi::class)
class DefaultMainComponent(
    componentContext: ComponentContext,
    private val storeFactory: StoreFactory,
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository,
    private val userDataStoreRepository: UserDataStoreRepository,
    private val appThemeDataStoreRepository: AppThemeDataStoreRepository,
    private val appSettingsRepository: AppSettingsRepository,
    private val clearUserData: () -> Unit,
    private val navigateToAuth: () -> Unit,
) : ComponentContext by componentContext, MainComponent {

    private val navigation = StackNavigation<Config>()
    private val userDataFlow = userDataStoreRepository.getUserData

    override val stack = childStack(
        source = navigation,
        initialConfiguration = Config.Home,
        serializer = Config.serializer(),
        handleBackButton = true,
        childFactory = ::child
    )

    private fun child(
        config: Config,
        componentContext: ComponentContext
    ): MainComponent.Child =
        when (config) {
            is Config.Home -> MainComponent.Child.Home(homeComponent(componentContext))
            is Config.Search -> MainComponent.Child.Search(searchComponent(componentContext))
            is Config.Profile -> MainComponent.Child.Profile(profileComponent(componentContext))
            is Config.People -> MainComponent.Child.People(peopleComponent(componentContext))
            is Config.Chat -> MainComponent.Child.Chat(
                chatComponent(
                    componentContext = componentContext,
                    config = config
                )
            )
            is Config.Settings -> MainComponent.Child.Settings(settingsComponent(componentContext))
            is Config.Help -> MainComponent.Child.Help(helpComponent(componentContext))
        }

    private fun homeComponent(componentContext: ComponentContext) =
        DefaultHomeComponent(
            componentContext = componentContext,
            storeFactory = storeFactory,
            chatRepository = chatRepository,
            appThemeDataStoreRepository = appThemeDataStoreRepository,
            appSettingsRepository = appSettingsRepository,
            userDataFlow = userDataFlow,
            clearUserData = clearUserData,
            navigateToSearch = { navigation.push(Config.Search) },
            navigateToProfile = { navigation.push(Config.Profile) },
            navigateToPeople = { navigation.push(Config.People) },
            navigateToSettings = { navigation.push(Config.Settings) },
            navigateToHelp = { navigation.push(Config.Help) },
            navigateToAuth = navigateToAuth,
            navigateToChat = { navigation.push(Config.Chat(it)) }
        )

    private fun searchComponent(componentContext: ComponentContext) =
        DefaultSearchComponent(
            componentContext = componentContext,
            storeFactory = storeFactory,
            userRepository = userRepository,
            userDataFlow = userDataFlow,
            chatRepository = chatRepository,
            navigateToChat = { navigation.push(Config.Chat(it)) },
            navigateBack = { navigation.pop() }
        )

    private fun profileComponent(componentContext: ComponentContext) =
        DefaultProfileComponent(
            componentContext = componentContext,
            storeFactory = storeFactory,
            userRepository = userRepository,
            userDataStoreRepository = userDataStoreRepository,
            navigateBack = { navigation.pop() }
        )

    private fun peopleComponent(componentContext: ComponentContext) =
        DefaultPeopleComponent(
            componentContext = componentContext,
            storeFactory = storeFactory,
            userRepository = userRepository,
            userDataFlow = userDataFlow,
            chatRepository = chatRepository,
            navigateToChat = { navigation.push(Config.Chat(it)) },
            navigateBack = { navigation.pop() }
        )

    private fun chatComponent(
        componentContext: ComponentContext,
        config: Config.Chat
    ) =
        DefaultChatComponent(
            componentContext = componentContext,
            storeFactory = storeFactory,
            chatRepository = chatRepository,
            userDataFlow = userDataFlow,
            chatId = config.id,
            navigateBack = { navigation.pop() }
        )

    private fun settingsComponent(componentContext: ComponentContext) =
        DefaultSettingsComponent(
            componentContext = componentContext,
            storeFactory = storeFactory,
            userDataFlow = userDataFlow,
            appThemeDataStoreRepository = appThemeDataStoreRepository,
            appSettingsRepository = appSettingsRepository,
            navigateToHelp = { navigation.push(Config.Help) },
            navigateBack = { navigation.pop() }
        )

    private fun helpComponent(componentContext: ComponentContext) =
        DefaultHelpComponent(
            componentContext = componentContext,
            onBack = { navigation.pop() }
        )

    @Serializable
    sealed interface Config {
        @Serializable
        data object Home : Config

        @Serializable
        data object Search : Config

        @Serializable
        data object Profile : Config

        @Serializable
        data object People : Config

        @Serializable
        class Chat(val id: String) : Config

        @Serializable
        data object Settings : Config

        @Serializable
        data object Help : Config
    }
}
