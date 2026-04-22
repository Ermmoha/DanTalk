package com.example.feature.main.profile.util

import kotlinx.serialization.Serializable

@Serializable
sealed interface ProfileValidation {
    data object Valid: ProfileValidation
    data object EmptyUsername: ProfileValidation
    data object EmptyEmail: ProfileValidation
    data object EmptyFirstname: ProfileValidation
    data object EmailExists: ProfileValidation
    data object UsernameExists: ProfileValidation
    data object InvalidEmailFormat: ProfileValidation
}