package com.example.feature.main.people.store

import android.content.Context
import com.arkivanov.mvikotlin.core.store.Store
import com.example.core.ui.model.UiUserData
import com.example.feature.main.people.store.PeopleStore.*
import com.example.data.user.api.model.UserData

interface PeopleStore : Store<Intent, State, Label> {

    sealed interface Intent {
        class OnQueryChange(val query: String) : Intent
        class OpenChat(val userId: String) : Intent
        class DownloadImage(val context: Context, val url: String) : Intent
        data object NavigateBack : Intent
    }

    data class State(
        val query: String = "",
        val usersByQuery: List<UiUserData> = emptyList(),
        val isLoading: Boolean = false,
        val currentUserId: String = ""
    )

    sealed interface Label {
        class OpenChat(val chatId: String) : Label
        data object NavigateBack : Label
    }
}