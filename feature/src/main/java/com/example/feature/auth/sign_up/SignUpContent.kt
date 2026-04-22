package com.example.feature.auth.sign_up

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.example.feature.auth.sign_up.component.SignUpComponent
import com.example.feature.auth.sign_up.input_email.InputEmailContent
import com.example.feature.auth.sign_up.input_name.InputNameContent
import com.example.feature.auth.sign_up.input_password.InputPasswordContent

@Composable
fun SignUpContent(
    component: SignUpComponent
) {
    val stack = component.stack

    Children(
        stack = stack,
        animation = stackAnimation(slide())
    ) { child ->
        when(val instance = child.instance) {
            is SignUpComponent.Child.InputEmail -> InputEmailContent(instance.component)
            is SignUpComponent.Child.InputName -> InputNameContent(instance.component)
            is SignUpComponent.Child.InputPassword -> InputPasswordContent(instance.component)
        }
    }
}