package com.example.feature.auth.sign_up.input_name.store

import com.arkivanov.mvikotlin.core.store.Store
import com.example.data.user.api.model.UserData
import com.example.feature.auth.sign_up.input_name.store.InputNameStore.Intent
import com.example.feature.auth.sign_up.input_name.store.InputNameStore.Label
import com.example.feature.auth.sign_up.input_name.store.InputNameStore.State

interface InputNameStore : Store<Intent, State, Label> {

    sealed interface Intent {
        class OnFirstnameChange(val firstname: String) : Intent
        class OnLastnameChange(val lastname: String) : Intent
        class OnPatronymicChange(val patronymic: String) : Intent
        data object NavigateNext : Intent
        data object NavigateBack : Intent
    }

    data class State(
        val firstname: String = "",
        val lastname: String = "",
        val patronymic: String = "",
        val isEmptyFirstname: Boolean = false,
    )

    sealed interface Label {
        class NavigateNext(val userData: UserData) : Label
        data object NavigateBack : Label
    }
}