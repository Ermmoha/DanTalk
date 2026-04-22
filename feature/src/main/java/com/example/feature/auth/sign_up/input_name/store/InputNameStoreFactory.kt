package com.example.feature.auth.sign_up.input_name.store

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import com.example.data.user.api.model.UserData
import com.example.feature.auth.sign_up.input_name.store.InputNameStore.Intent
import com.example.feature.auth.sign_up.input_name.store.InputNameStore.Label
import com.example.feature.auth.sign_up.input_name.store.InputNameStore.State


class InputNameStoreFactory(
    private val factory: StoreFactory,
    private val currentUserData: UserData,
) {
    private sealed interface Msg {
        class OnFirstnameChange(val firstname: String) : Msg
        class OnLastnameChange(val lastname: String) : Msg
        class OnPatronymicChange(val patronymic: String) : Msg
        class UpdateIsEmptyFirstname(val isEmptyFirstname: Boolean) : Msg
    }

    fun create(): InputNameStore =
        object : InputNameStore,
            Store<Intent, State, Label> by factory.create<Intent, Nothing, Msg, State, Label>(
                name = "InputNameStore",
                initialState = State(),
                executorFactory = coroutineExecutorFactory {
                    onIntent<Intent.OnFirstnameChange> { dispatch(Msg.OnFirstnameChange(it.firstname)) }
                    onIntent<Intent.OnLastnameChange> { dispatch(Msg.OnLastnameChange(it.lastname)) }
                    onIntent<Intent.OnPatronymicChange> { dispatch(Msg.OnPatronymicChange(it.patronymic)) }
                    onIntent<Intent.NavigateNext> {
                        val isEmptyFirstname = state().firstname.isBlank()
                        dispatch(Msg.UpdateIsEmptyFirstname(isEmptyFirstname))
                        if (state().isEmptyFirstname) return@onIntent
                        val userData = currentUserData.copy(
                            firstname = state().firstname,
                            lastname = state().lastname,
                            patronymic = state().patronymic
                        )
                        publish(Label.NavigateNext(userData))
                    }
                    onIntent<Intent.NavigateBack> { publish(Label.NavigateBack) }
                },
                reducer = { msg ->
                    when (msg) {
                        is Msg.OnFirstnameChange -> copy(firstname = msg.firstname)
                        is Msg.OnLastnameChange -> copy(lastname = msg.lastname)
                        is Msg.OnPatronymicChange -> copy(patronymic = msg.patronymic)
                        is Msg.UpdateIsEmptyFirstname -> copy(isEmptyFirstname = msg.isEmptyFirstname)
                    }
                }
            ) {}
}