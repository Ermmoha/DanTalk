package com.example.core.ui.util

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType

data class InputFormField(
    val title: String,
    val value: String,
    val onValueChange: (String) -> Unit,
    val isPassword: Boolean = false,
    val isError: Boolean = false,
    val keyboardOptions: KeyboardOptions = KeyboardOptions(
        imeAction = ImeAction.Next,
        keyboardType = KeyboardType.Text
    )
)
