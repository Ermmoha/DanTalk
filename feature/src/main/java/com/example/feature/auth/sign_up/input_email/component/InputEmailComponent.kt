package com.example.feature.auth.sign_up.input_email.component

import com.example.feature.auth.sign_up.input_email.store.InputEmailStore
import kotlinx.coroutines.flow.StateFlow

interface InputEmailComponent {
    val state: StateFlow<InputEmailStore.State>

    fun onIntent(intent: InputEmailStore.Intent)
}