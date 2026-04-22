package com.example.feature.auth.sign_up.input_password.store

import com.arkivanov.mvikotlin.core.store.Store
import com.example.feature.auth.sign_up.input_password.store.InputPasswordStore.Intent
import com.example.feature.auth.sign_up.input_password.store.InputPasswordStore.Label
import com.example.feature.auth.sign_up.input_password.store.InputPasswordStore.State
import com.example.feature.auth.sign_up.input_password.util.InputPasswordValidation

interface InputPasswordStore : Store<Intent, State, Label> {

    sealed interface Intent {
        class OnPasswordChange(val password: String) : Intent
        class OnRepeatablePasswordChange(val repeatablePassword: String) : Intent
        data object SignUp : Intent
        data object DismissDialog : Intent
        data object NavigateBack : Intent
    }

    data class State(
        val password: String = "",
        val repeatablePassword: String = "",
        val validation: InputPasswordValidation = InputPasswordValidation.Valid,
        val isLoading: Boolean = false,
        val isSuccessful: Boolean = false,
    )

    sealed interface Label {
        data object NavigateToHome : Label
        data object NavigateBack : Label
    }
}