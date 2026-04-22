package com.example.feature.auth.sign_in.util

import kotlinx.serialization.Serializable

@Serializable
sealed interface SignInValidation {
    data object Valid: SignInValidation
    data object EmptyEmail: SignInValidation
    data object EmptyPassword: SignInValidation
    data object EmptyAllFields: SignInValidation
    data object InvalidEmailFormat: SignInValidation
    data object NetworkError: SignInValidation
    data object InvalidCredentials: SignInValidation
}