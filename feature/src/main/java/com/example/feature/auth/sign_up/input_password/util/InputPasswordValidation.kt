package com.example.feature.auth.sign_up.input_password.util

import kotlinx.serialization.Serializable

@Serializable
sealed class InputPasswordValidation {
    data object Valid: InputPasswordValidation()
    data object EmptyPassword: InputPasswordValidation()
    data object NotMatchesPasswords: InputPasswordValidation()
    data object PasswordIsTooShort: InputPasswordValidation()
    data object NetworkError: InputPasswordValidation()
}