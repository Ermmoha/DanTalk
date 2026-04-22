package com.example.feature.auth.sign_up.input_name.component

import com.example.feature.auth.sign_up.input_name.store.InputNameStore
import kotlinx.coroutines.flow.StateFlow

interface InputNameComponent {
    val state: StateFlow<InputNameStore.State>

    fun onIntent(intent: InputNameStore.Intent)
}