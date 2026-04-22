package com.example.feature.main.settings.store

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import com.example.core.design.AppTheme
import com.example.core.ui.model.UiUserData
import com.example.data.app_theme.api.AppThemeDataStoreRepository
import com.example.data.settings.api.AppSettingsRepository
import com.example.data.user.api.model.UserData
import com.example.feature.main.settings.store.SettingsStore.Intent
import com.example.feature.main.settings.store.SettingsStore.Label
import com.example.feature.main.settings.store.SettingsStore.State
import com.example.feature.mapper.toUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsStoreFactory(
    private val factory: StoreFactory,
    private val userDataFlow: Flow<UserData>,
    private val appThemeDataStoreRepository: AppThemeDataStoreRepository,
    private val appSettingsRepository: AppSettingsRepository
) {
    private sealed interface Action {
        class SetUser(val user: UiUserData) : Action
        class SetTheme(val theme: AppTheme) : Action
        class SetNotificationsEnabled(val enabled: Boolean) : Action
    }

    private sealed interface Msg {
        class SetUser(val user: UiUserData) : Msg
        class SetTheme(val theme: AppTheme) : Msg
        class SetNotificationsEnabled(val enabled: Boolean) : Msg
    }

    fun create(): SettingsStore =
        object : SettingsStore,
            Store<Intent, State, Label> by factory.create<Intent, Action, Msg, State, Label>(
                name = "SettingsStore",
                initialState = State(),
                bootstrapper = coroutineBootstrapper {
                    launch {
                        userDataFlow
                            .map { it.toUi() }
                            .collect { dispatch(Action.SetUser(it)) }
                    }
                    launch {
                        appThemeDataStoreRepository.themeFlow.collect {
                            dispatch(Action.SetTheme(it))
                        }
                    }
                    launch {
                        appSettingsRepository.notificationsEnabledFlow.collect {
                            dispatch(Action.SetNotificationsEnabled(it))
                        }
                    }
                },
                executorFactory = coroutineExecutorFactory {
                    onAction<Action.SetUser> { dispatch(Msg.SetUser(it.user)) }
                    onAction<Action.SetTheme> { dispatch(Msg.SetTheme(it.theme)) }
                    onAction<Action.SetNotificationsEnabled> {
                        dispatch(Msg.SetNotificationsEnabled(it.enabled))
                    }
                    onIntent<Intent.ChangeTheme> { launch { changeTheme(it.theme) } }
                    onIntent<Intent.SetNotificationsEnabled> {
                        launch { setNotificationsEnabled(it.enabled) }
                    }
                    onIntent<Intent.NavigateToHelp> { publish(Label.NavigateToHelp) }
                    onIntent<Intent.NavigateBack> { publish(Label.NavigateBack) }
                },
                reducer = { msg ->
                    when (msg) {
                        is Msg.SetUser -> copy(user = msg.user)
                        is Msg.SetTheme -> copy(selectedTheme = msg.theme)
                        is Msg.SetNotificationsEnabled -> copy(notificationsEnabled = msg.enabled)
                    }
                }
            ) {}

    private suspend fun changeTheme(theme: AppTheme) = withContext(Dispatchers.IO) {
        appThemeDataStoreRepository.setTheme(theme)
    }

    private suspend fun setNotificationsEnabled(enabled: Boolean) = withContext(Dispatchers.IO) {
        appSettingsRepository.setNotificationsEnabled(enabled)
    }
}
