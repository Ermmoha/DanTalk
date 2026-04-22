package com.example.feature.main.home.store

import com.arkivanov.mvikotlin.core.store.Store
import com.example.core.design.AppTheme
import com.example.core.ui.model.UiChat
import com.example.core.ui.model.UiUserData
import com.example.feature.main.home.store.HomeStore.Intent
import com.example.feature.main.home.store.HomeStore.Label
import com.example.feature.main.home.store.HomeStore.State

interface HomeStore : Store<Intent, State, Label> {

    sealed interface Intent {
        data object NavigateToSearch : Intent
        data object NavigateToProfile : Intent
        data object NavigateToPeople : Intent
        data object NavigateToSettings : Intent
        data object NavigateToHelp : Intent
        class OpenChat(val id: String) : Intent
        class DeleteChat(val id: String) : Intent
        class TogglePinChat(val id: String) : Intent
        class ChangeAppTheme(val theme: AppTheme) : Intent
        data object SignOut : Intent
    }

    data class State(
        val chats: List<UiChat> = emptyList(),
        val user: UiUserData = UiUserData(),
        val isLoading: Boolean = false
    )

    sealed interface Label {
        data object NavigateToSearch : Label
        data object NavigateToProfile : Label
        data object NavigateToPeople : Label
        data object NavigateToSettings : Label
        data object NavigateToHelp : Label
        data object NavigateToAuth : Label
        class NavigateToChat(val id: String) : Label
    }
}
