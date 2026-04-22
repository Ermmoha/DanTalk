package com.example.feature.main.search.store

import com.arkivanov.mvikotlin.core.store.Store
import com.example.core.ui.model.UiChat
import com.example.feature.main.search.store.SearchStore.Intent
import com.example.feature.main.search.store.SearchStore.Label
import com.example.feature.main.search.store.SearchStore.State
import com.example.data.user.api.model.UserData
import com.example.feature.main.people.store.PeopleStore

interface SearchStore : Store<Intent, State, Label> {

    sealed interface Intent {
        class OnQueryChange(val query: String) : Intent
        class OpenChat(val chatId: String) : Intent
        data object NavigateBack : Intent
    }

    data class State(
        val query: String = "",
        val chatsByQuery: List<UiChat> = emptyList(),
        val isLoading: Boolean = false
    )

    sealed interface Label {
        class OpenChat(val chatId: String) : Label
        data object NavigateBack : Label
    }
}