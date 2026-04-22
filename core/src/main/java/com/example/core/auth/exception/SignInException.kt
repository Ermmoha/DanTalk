package com.example.core.auth.exception

sealed class SignInException : Exception() {
    class InvalidCredentials : SignInException()
    class InvalidEmailFormat : SignInException()
    class NetworkError : SignInException()
}