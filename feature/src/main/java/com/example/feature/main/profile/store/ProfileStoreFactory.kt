package com.example.feature.main.profile.store

import android.util.Patterns
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutorScope
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import com.example.background.service.ImageLoadServiceStarter
import com.example.core.ui.model.UiUserData
import com.example.data.user.api.UserDataStoreRepository
import com.example.data.user.api.UserRepository
import com.example.data.user.api.model.UserData
import com.example.feature.main.profile.store.ProfileStore.Intent
import com.example.feature.main.profile.store.ProfileStore.Label
import com.example.feature.main.profile.store.ProfileStore.State
import com.example.feature.main.profile.util.ProfileValidation
import com.example.feature.mapper.toUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileStoreFactory(
    private val factory: StoreFactory,
    private val userRepository: UserRepository,
    private val userDataStoreRepository: UserDataStoreRepository,
) {
    sealed interface Action {
        class SetUser(val user: UiUserData) : Action
    }

    sealed interface Msg {
        class SetUser(val userData: UiUserData) : Msg
        class UpdateNewUserData(val newUserData: UiUserData) : Msg
        class UpdateValidation(val validation: ProfileValidation) : Msg
    }

    fun create(): ProfileStore =
        object : ProfileStore,
            Store<Intent, State, Label> by factory.create<Intent, Action, Msg, State, Label>(
                name = "ProfileStore",
                initialState = State(),
                bootstrapper = coroutineBootstrapper {
                    launch(Dispatchers.IO) {
                        userDataStoreRepository.getUserData.collect { user ->
                            withContext(Dispatchers.Main) {
                                dispatch(Action.SetUser(user.toUi()))
                            }
                        }
                    }
                },
                executorFactory = coroutineExecutorFactory {
                    onAction<Action.SetUser> { dispatch(Msg.SetUser(it.user)) }
                    onIntent<Intent.UpdateNewUserData> { dispatch(Msg.UpdateNewUserData(it.newUserData)) }
                    onIntent<Intent.SaveNewUserData> { saveNewUserData() }
                    onIntent<Intent.LoadImageIntoStorage> { ImageLoadServiceStarter.postAvatar(it.context, it.uri) }
                    onIntent<Intent.NavigateBack> { publish(Label.NavigateBack) }
                },
                reducer = { msg ->
                    when (msg) {
                        is Msg.SetUser -> copy(
                            currentUser = msg.userData,
                            newUserData = msg.userData
                        )

                        is Msg.UpdateNewUserData -> copy(newUserData = msg.newUserData)
                        is Msg.UpdateValidation -> copy(validation = msg.validation)
                    }
                }
            ) {}

    private fun CoroutineExecutorScope<State, Msg, Nothing, Nothing>.saveNewUserData() {
        if (state().currentUser != null)
            launch {
                dispatch(
                    Msg.UpdateValidation(
                        validateInput(
                            state().newUserData,
                            state().currentUser!!
                        )
                    )
                )
                if (state().validation !is ProfileValidation.Valid) return@launch
                val updatedUserData =
                    state().newUserData.toUserData().copy(id = state().currentUser!!.id)
                saveNewUserData(updatedUserData)
            }
    }

    private suspend fun validateInput(
        newUserData: UiUserData,
        currentUser: UiUserData,
    ): ProfileValidation = withContext(Dispatchers.IO) {
        when {
            newUserData.email.isEmpty() -> ProfileValidation.EmptyEmail
            newUserData.username.isEmpty() -> ProfileValidation.EmptyUsername
            newUserData.firstname.isEmpty() -> ProfileValidation.EmptyFirstname

            !Patterns.EMAIL_ADDRESS.matcher(
                newUserData.email
            ).matches() -> ProfileValidation.InvalidEmailFormat

            newUserData.username != currentUser.username &&
                    userRepository.isValueExists(
                        field = "username",
                        value = newUserData.username
                    ) -> ProfileValidation.UsernameExists

            newUserData.email != currentUser.email &&
                    userRepository.isValueExists(
                        field = "email",
                        value = newUserData.email
                    ) -> ProfileValidation.EmailExists

            else -> ProfileValidation.Valid
        }
    }

    private suspend fun saveNewUserData(newUserData: UserData) = withContext(Dispatchers.IO) {
        userRepository.updateUser(newUserData)
        userDataStoreRepository.saveUserData(newUserData)
    }

    private fun UiUserData.toUserData(): UserData =
        UserData(id, avatar, email, username, firstname, lastname, patronymic)
}