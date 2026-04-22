package com.example.feature.auth.sign_up.input_email.store

import android.util.Patterns
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import com.example.data.user.api.UserRepository
import com.example.data.user.api.model.UserData
import com.example.feature.auth.sign_up.input_email.store.InputEmailStore.Intent
import com.example.feature.auth.sign_up.input_email.store.InputEmailStore.Label
import com.example.feature.auth.sign_up.input_email.store.InputEmailStore.State
import com.example.feature.auth.sign_up.input_email.util.InputEmailValidation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InputEmailStoreFactory(
    private val factory: StoreFactory,
    private val userRepository: UserRepository,
) {
    private sealed interface Msg {
        class OnEmailChange(val email: String) : Msg
        class OnUsernameChange(val username: String) : Msg
        class UpdateValidation(val validation: InputEmailValidation) : Msg
        class UpdateLoading(val isLoading: Boolean) : Msg
    }

    fun create(): InputEmailStore =
        object : InputEmailStore,
            Store<Intent, State, Label> by factory.create<Intent, Nothing, Msg, State, Label>(
                name = "InputEmailStore",
                initialState = State(),
                executorFactory = coroutineExecutorFactory {
                    onIntent<Intent.OnEmailChange> { dispatch(Msg.OnEmailChange(it.email)) }
                    onIntent<Intent.OnUsernameChange> { dispatch(Msg.OnUsernameChange(it.username)) }
                    onIntent<Intent.NavigateNext> {
                        validateUserData(state().email, state().username)
                            .let { dispatch(Msg.UpdateValidation(it)) }
                        if (state().validation != InputEmailValidation.Valid) return@onIntent
                        launch {
                            dispatch(Msg.UpdateLoading(true))
                            val validation = checkFieldsExists(state().email, state().username)
                            dispatch(Msg.UpdateValidation(validation))
                            dispatch(Msg.UpdateLoading(false))
                        }
                        if (state().validation == InputEmailValidation.Valid) {
                            val userData =
                                UserData(email = state().email, username = state().username)
                            publish(Label.NavigateNext(userData))
                        }
                    }
                    onIntent<Intent.NavigateBack> { publish(Label.NavigateToSignIn) }
                },
                reducer = { msg ->
                    when (msg) {
                        is Msg.OnEmailChange -> copy(email = msg.email)
                        is Msg.OnUsernameChange -> copy(username = msg.username)
                        is Msg.UpdateValidation -> copy(validation = msg.validation)
                        is Msg.UpdateLoading -> copy(isLoading = msg.isLoading)
                    }
                }
            ) {}

    private fun validateUserData(email: String, username: String): InputEmailValidation =
        when {
            email.isBlank() && username.isBlank() -> InputEmailValidation.EmptyAllFields
            email.isBlank() -> InputEmailValidation.EmptyEmail
            username.isBlank() -> InputEmailValidation.EmptyUsername
            !Patterns.EMAIL_ADDRESS.matcher(email)
                .matches() -> InputEmailValidation.InvalidEmailFormat

            else -> InputEmailValidation.Valid
        }

    private suspend fun checkFieldsExists(email: String, username: String): InputEmailValidation =
        withContext(Dispatchers.IO) {
            when {
                userRepository.isValueExists(
                    "email",
                    email
                ) -> InputEmailValidation.EmailAlreadyExist

                userRepository.isValueExists(
                    "username",
                    username
                ) -> InputEmailValidation.UsernameAlreadyExist

                else -> InputEmailValidation.Valid
            }
        }
}