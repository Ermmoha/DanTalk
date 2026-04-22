package com.example.feature.root

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.example.core.design.AppTheme
import com.example.core.design.theme.DanTalkTheme
import com.example.feature.auth.AuthContent
import com.example.feature.main.MainContent
import com.example.feature.root.component.RootComponent

@Composable
fun RootContent(
    component: RootComponent,
) {
    val stack = component.stack
    val themeState = component.themeState.collectAsState()

    DanTalkTheme(
        darkTheme = when (themeState.value) {
            AppTheme.DARK -> true
            AppTheme.SYSTEM -> isSystemInDarkTheme()
            else -> false
        }
    ) {
        Children(
            stack = stack,
            animation = stackAnimation(fade() + scale())
        ) { child ->
            when (val instance = child.instance) {
                is RootComponent.Child.Auth -> AuthContent(instance.component)
                is RootComponent.Child.Main -> MainContent(instance.component, themeState)
            }
        }
    }
}