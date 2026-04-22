package com.example.data.user.api.model

import kotlinx.serialization.Serializable

@Serializable
data class UserData(
    val id: String = "",
    val avatar: String = "",
    val email: String = "",
    val username: String = "",
    val firstname: String = "",
    val lastname: String = "",
    val patronymic: String = ""
)