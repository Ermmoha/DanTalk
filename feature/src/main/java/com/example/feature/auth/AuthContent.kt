package com.example.feature.auth

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.example.feature.auth.component.AuthComponent
import com.example.feature.auth.sign_in.SignInContent
import com.example.feature.auth.sign_up.SignUpContent

@Composable
fun AuthContent(
    component: AuthComponent
) {
    val stack = component.stack

    Children(
        stack = stack,
        animation = stackAnimation(
            scale(
                animationSpec = tween(200, easing = LinearEasing),
                frontFactor = 1.15f, backFactor = 1.3f
            ) + fade(
                animationSpec = tween(easing = LinearOutSlowInEasing),
                minAlpha = 0.4f
            )
        )
    ) { child ->
        when(val instance = child.instance) {
            is AuthComponent.Child.SignIn -> SignInContent(instance.component)
            is AuthComponent.Child.SignUp -> SignUpContent(instance.component)
        }
    }
}