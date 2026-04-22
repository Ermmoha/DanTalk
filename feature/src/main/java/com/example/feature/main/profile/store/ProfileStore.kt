package com.example.feature.main.profile.store

import android.content.Context
import android.net.Uri
import com.arkivanov.mvikotlin.core.store.Store
import com.example.core.ui.model.UiUserData
import com.example.feature.main.profile.store.ProfileStore.Intent
import com.example.feature.main.profile.store.ProfileStore.Label
import com.example.feature.main.profile.store.ProfileStore.State
import com.example.data.user.api.model.UserData
import com.example.feature.main.profile.util.ProfileValidation

interface ProfileStore : Store<Intent, State, Label> {

    sealed interface Intent {
        class UpdateNewUserData(val newUserData: UiUserData) : Intent
        data object SaveNewUserData : Intent
        class LoadImageIntoStorage(val context: Context, val uri: Uri) : Intent
        data object NavigateBack : Intent
    }

    data class State(
        val currentUser: UiUserData? = null,
        val newUserData: UiUserData = UiUserData(),
        val validation: ProfileValidation = ProfileValidation.Valid
    )

    sealed interface Label {
        data object NavigateBack : Label
    }
}