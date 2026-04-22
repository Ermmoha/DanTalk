package com.example.feature.auth.sign_in.component

import com.example.feature.auth.sign_in.store.SignInStore
import kotlinx.coroutines.flow.StateFlow

interface SignInComponent {
   val state: StateFlow<SignInStore.State>

   fun onIntent(intent: SignInStore.Intent)
}