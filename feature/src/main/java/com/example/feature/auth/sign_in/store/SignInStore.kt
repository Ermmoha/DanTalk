package com.example.feature.auth.sign_in.store

import com.arkivanov.mvikotlin.core.store.Store
import com.example.feature.auth.sign_in.store.SignInStore.Intent
import com.example.feature.auth.sign_in.store.SignInStore.Label
import com.example.feature.auth.sign_in.store.SignInStore.State
import com.example.feature.auth.sign_in.util.SignInValidation

interface SignInStore : Store<Intent, State, Label> {

    sealed interface Intent {
        class OnEmailChange(val email: String) : Intent
        class OnPasswordChange(val password: String) : Intent
        data object SignIn : Intent
        data object NavigateToSignUp : Intent
        data object DismissDialog : Intent
    }

    data class State(
        val email: String = "",
        val password: String = "",
        val validation: SignInValidation = SignInValidation.Valid,
        val isLoading: Boolean = false,
        val isSuccessful: Boolean = false,
    )

    sealed interface Label {
        data object NavigateToSignUp : Label
        data object NavigateToHome : Label
    }
}