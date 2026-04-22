package com.example.feature.auth.sign_up.input_email

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.example.core.design.theme.DanTalkTheme
import com.example.core.ui.components.snackbar.CustomSnackbarHost
import com.example.core.ui.util.InputFormField
import com.example.feature.auth.sign_up.input_email.component.InputEmailComponent
import com.example.feature.auth.sign_up.input_email.store.InputEmailStore
import com.example.feature.auth.sign_up.input_email.util.InputEmailValidation
import com.example.feature.auth.ui.components.AuthForm

@Composable
fun InputEmailContent(
    component: InputEmailComponent,
) {
    val state by component.state.collectAsState()

    Content(
        state = state,
        onIntent = { component.onIntent(it) }
    )
}

@Composable
private fun Content(
    state: InputEmailStore.State,
    onIntent: (InputEmailStore.Intent) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val fields = listOf(
        InputFormField(
            title = "Email",
            value = state.email,
            onValueChange = { onIntent(InputEmailStore.Intent.OnEmailChange(it)) },
            isError = state.validation == InputEmailValidation.EmptyEmail
                    || state.validation == InputEmailValidation.EmptyAllFields
                    || state.validation == InputEmailValidation.InvalidEmailFormat
                    || state.validation == InputEmailValidation.EmailAlreadyExist,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Email
            )
        ),
        InputFormField(
            title = "Имя пользователя",
            value = state.username,
            onValueChange = { onIntent(InputEmailStore.Intent.OnUsernameChange(it)) },
            isError = state.validation == InputEmailValidation.EmptyUsername
                    || state.validation == InputEmailValidation.EmptyAllFields
                    || state.validation == InputEmailValidation.UsernameAlreadyExist,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Text
            )
        )
    )

    LaunchedEffect(state.validation) {
        if (state.validation != InputEmailValidation.Valid) {
            snackbarHostState.showSnackbar(
                message = when (state.validation) {
                    is InputEmailValidation.EmptyUsername -> "Имя пользователя не может быть пустым"
                    is InputEmailValidation.EmptyEmail -> "Почта не может быть пустой"
                    is InputEmailValidation.EmptyAllFields -> "Заполните все поля"
                    is InputEmailValidation.InvalidEmailFormat -> "Неверный формат почты"
                    is InputEmailValidation.UsernameAlreadyExist -> "Пользователь с таким именем уже существует"
                    is InputEmailValidation.EmailAlreadyExist -> "Пользователь с такой почтой уже существует"
                    else -> ""
                },
                duration = SnackbarDuration.Short
            )
        }
    }

    AuthForm(
        snackbarHost = { CustomSnackbarHost(snackbarHostState, DanTalkTheme.colors.singleTheme) },
        fields = fields,
        onMainButtonClick = { onIntent(InputEmailStore.Intent.NavigateNext) },
        onBottomTextButtonClick = { onIntent(InputEmailStore.Intent.NavigateBack) },
        title = Pair("Регистрация", "Введите имя пользователя\nи электронную почту"),
        isLoading = state.isLoading,
        buttonText = "Продолжить",
        bottomButtonText = "Войти в существующий аккаунт"
    )
}