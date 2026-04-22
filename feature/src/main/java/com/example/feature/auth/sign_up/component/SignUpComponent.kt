package com.example.feature.auth.sign_up.component

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.example.feature.auth.sign_up.input_email.component.InputEmailComponent
import com.example.feature.auth.sign_up.input_name.component.InputNameComponent
import com.example.feature.auth.sign_up.input_password.component.InputPasswordComponent

interface SignUpComponent {
    val stack: Value<ChildStack<*, Child>>

    sealed interface Child {
        class InputEmail(val component: InputEmailComponent) : Child
        class InputName(val component: InputNameComponent) : Child
        class InputPassword(val component: InputPasswordComponent) : Child
    }
}