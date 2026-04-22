package com.example.core.auth.exception

sealed class SignUpException : Exception() {
    class WeakPasswordException : SignUpException()
    class NetworkException : SignUpException()
}