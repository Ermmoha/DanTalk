package com.example.feature.root.component

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.example.core.design.AppTheme
import com.example.feature.auth.component.AuthComponent
import com.example.feature.main.component.MainComponent
import kotlinx.coroutines.flow.StateFlow

interface RootComponent {
    val stack: Value<ChildStack<*, Child>>

    val themeState: StateFlow<AppTheme>

    sealed interface Child {
        class Auth(val component: AuthComponent) : Child
        class Main(val component: MainComponent) : Child
    }
}