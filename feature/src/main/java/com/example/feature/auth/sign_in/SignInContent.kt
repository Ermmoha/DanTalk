package com.example.feature.auth.sign_in

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
import com.example.core.ui.components.DialogueBox
import com.example.core.ui.components.snackbar.CustomSnackbarHost
import com.example.core.ui.util.InputFormField
import com.example.feature.auth.sign_in.component.SignInComponent
import com.example.feature.auth.sign_in.store.SignInStore
import com.example.feature.auth.sign_in.util.SignInValidation
import com.example.feature.auth.ui.components.AuthForm

@Composable
fun SignInContent(
    component: SignInComponent,
) {
    val state by component.state.collectAsState()

    Content(
        state = state,
        onIntent = { component.onIntent(it) }
    )
}

@Composable
private fun Content(
    state: SignInStore.State,
    onIntent: (SignInStore.Intent) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val fields = listOf(
        InputFormField(
            title = "Email",
            value = state.email,
            onValueChange = { onIntent(SignInStore.Intent.OnEmailChange(it)) },
            isError = state.validation == SignInValidation.EmptyEmail
                    || state.validation == SignInValidation.InvalidEmailFormat
                    || state.validation == SignInValidation.EmptyAllFields
                    || state.validation == SignInValidation.InvalidCredentials,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Email
            )
        ),
        InputFormField(
            title = "Пароль",
            value = state.password,
            onValueChange = { onIntent(SignInStore.Intent.OnPasswordChange(it)) },
            isPassword = true,
            isError = state.validation == SignInValidation.EmptyPassword
                    || state.validation == SignInValidation.EmptyAllFields
                    || state.validation == SignInValidation.InvalidCredentials,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Password
            )
        )
    )

    LaunchedEffect(state.validation) {
        if (state.validation != SignInValidation.Valid) {
            snackbarHostState.showSnackbar(
                message = when (state.validation) {
                    is SignInValidation.EmptyAllFields -> "Заполните все поля"
                    is SignInValidation.EmptyEmail -> "Почта не может быть пустой"
                    is SignInValidation.EmptyPassword -> "Пароль не может быть пустым"
                    is SignInValidation.InvalidEmailFormat -> "Неверный формат почты"
                    is SignInValidation.NetworkError -> "Проверьте подключение к сети"
                    is SignInValidation.InvalidCredentials -> "Неверные учетные данные"
                    is SignInValidation.Valid -> ""
                },
                duration = SnackbarDuration.Short
            )
        }
    }

    if (state.isSuccessful)
        DialogueBox(
            onDismissRequest = { onIntent(SignInStore.Intent.DismissDialog) },
            text = "Вы успешно вошли в аккаунт!"
        )
    AuthForm(
        snackbarHost = { CustomSnackbarHost(snackbarHostState, DanTalkTheme.colors.singleTheme) },
        fields = fields,
        onMainButtonClick = { onIntent(SignInStore.Intent.SignIn) },
        onBottomTextButtonClick = { onIntent(SignInStore.Intent.NavigateToSignUp) },
        title = Pair("Авторизация", "Войдите в приложение,\nчтобы общаться с друзьями!"),
        isLoading = state.isLoading,
        buttonText = "Войти в аккаунт",
        bottomButtonText = "Создать аккаунт"
    )
}