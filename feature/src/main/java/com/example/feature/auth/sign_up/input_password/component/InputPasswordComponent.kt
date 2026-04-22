package com.example.feature.auth.sign_up.input_password.component

import com.example.feature.auth.sign_up.input_password.store.InputPasswordStore
import kotlinx.coroutines.flow.StateFlow

interface InputPasswordComponent {
    val state: StateFlow<InputPasswordStore.State>

    fun onIntent(intent: InputPasswordStore.Intent)
}