package com.example.feature.auth.sign_up.input_password.store

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutorScope
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import com.example.core.auth.exception.SignUpException
import com.example.data.auth.api.AuthRepository
import com.example.data.user.api.UserDataStoreRepository
import com.example.data.user.api.UserRepository
import com.example.data.user.api.model.UserData
import com.example.feature.auth.sign_up.input_password.store.InputPasswordStore.Intent
import com.example.feature.auth.sign_up.input_password.store.InputPasswordStore.Label
import com.example.feature.auth.sign_up.input_password.store.InputPasswordStore.State
import com.example.feature.auth.sign_up.input_password.util.InputPasswordValidation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InputPasswordStoreFactory(
    private val factory: StoreFactory,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val userDataStoreRepository: UserDataStoreRepository,
    private val currentUserData: UserData,
) {
    private sealed interface Msg {
        class OnPasswordChange(val password: String) : Msg
        class OnRepeatablePasswordChange(val repeatablePassword: String) : Msg
        class UpdateValidation(val validation: InputPasswordValidation) : Msg
        class UpdateLoading(
            val isLoading: Boolean = false,
            val isSuccessful: Boolean = false,
        ) : Msg
    }

    fun create(): InputPasswordStore =
        object : InputPasswordStore,
            Store<Intent, State, Label> by factory.create<Intent, Nothing, Msg, State, Label>(
                name = "InputPasswordStore",
                initialState = State(),
                executorFactory = coroutineExecutorFactory {
                    onIntent<Intent.OnPasswordChange> { dispatch(Msg.OnPasswordChange(it.password)) }
                    onIntent<Intent.OnRepeatablePasswordChange> { dispatch(Msg.OnRepeatablePasswordChange(it.repeatablePassword)) }
                    onIntent<Intent.SignUp> { signUp() }
                    onIntent<Intent.DismissDialog> {
                        dispatch(Msg.UpdateLoading(isSuccessful = false))
                        publish(Label.NavigateToHome)
                    }
                    onIntent<Intent.NavigateBack> { publish(Label.NavigateBack) }
                },
                reducer = { msg ->
                    when (msg) {
                        is Msg.OnPasswordChange -> copy(password = msg.password)
                        is Msg.OnRepeatablePasswordChange -> copy(repeatablePassword = msg.repeatablePassword)
                        is Msg.UpdateValidation -> copy(validation = msg.validation)
                        is Msg.UpdateLoading -> copy(
                            isLoading = msg.isLoading,
                            isSuccessful = msg.isSuccessful
                        )
                    }
                }
            ) {}

    private fun CoroutineExecutorScope<State, Msg, Nothing, Nothing>.signUp() {
        validatePassword(state().password, state().repeatablePassword)
            .let {
                dispatch(Msg.UpdateValidation(it))
                if (it != InputPasswordValidation.Valid) return
            }
        dispatch(Msg.UpdateLoading(true))
        launch {
            try {
                withContext(Dispatchers.IO) {
                    val userId = authRepository.createUser(currentUserData.email, state().password)
                    saveUserData(userId)
                }
                dispatch(Msg.UpdateLoading(isSuccessful = true))
            } catch (e: SignUpException) {
                val validation = when (e) {
                    is SignUpException.WeakPasswordException -> InputPasswordValidation.PasswordIsTooShort
                    is SignUpException.NetworkException -> InputPasswordValidation.NetworkError
                    else -> InputPasswordValidation.NetworkError
                }
                dispatch(Msg.UpdateLoading(isLoading = false))
                dispatch(Msg.UpdateValidation(validation))
            }
        }
    }

    private fun validatePassword(
        password: String,
        repeatablePassword: String,
    ): InputPasswordValidation =
        when {
            password.isBlank() -> InputPasswordValidation.EmptyPassword
            password != repeatablePassword -> InputPasswordValidation.NotMatchesPasswords
            else -> InputPasswordValidation.Valid
        }

    private suspend fun saveUserData(userId: String) = withContext(Dispatchers.IO) {
        val userData = currentUserData.copy(id = userId)
        userRepository.createUser(userData)
        userDataStoreRepository.saveUserData(userData)
    }
}