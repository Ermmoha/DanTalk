package com.example.feature.auth.sign_up.input_name

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
import com.example.core.ui.components.topbar.ExtraTopBar
import com.example.core.ui.util.InputFormField
import com.example.feature.auth.sign_up.input_name.component.InputNameComponent
import com.example.feature.auth.sign_up.input_name.store.InputNameStore
import com.example.feature.auth.ui.components.AuthForm

@Composable
fun InputNameContent(
    component: InputNameComponent,
) {
    val state by component.state.collectAsState()

    Content(
        state = state,
        onIntent = { component.onIntent(it) }
    )
}

@Composable
private fun Content(
    state: InputNameStore.State,
    onIntent: (InputNameStore.Intent) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val fields = listOf(
        InputFormField(
            title = "Имя",
            value = state.firstname,
            onValueChange = { onIntent(InputNameStore.Intent.OnFirstnameChange(it)) },
            isError = state.isEmptyFirstname,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Text
            )
        ),
        InputFormField(
            title = "Фамилия (опционально)",
            value = state.lastname,
            onValueChange = { onIntent(InputNameStore.Intent.OnLastnameChange(it)) },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Text
            )
        ),
        InputFormField(
            title = "Отчество (опционально)",
            value = state.patronymic,
            onValueChange = { onIntent(InputNameStore.Intent.OnPatronymicChange(it)) },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Text
            )
        )
    )

    LaunchedEffect(state.isEmptyFirstname) {
        if (state.isEmptyFirstname)
            snackbarHostState.showSnackbar(
                message = "Имя не может быть пустым",
                duration = SnackbarDuration.Short
            )
    }

    AuthForm(
        topBar = {
            ExtraTopBar(
                navigateBack = { onIntent(InputNameStore.Intent.NavigateBack) }
            )
        },
        snackbarHost = { CustomSnackbarHost(snackbarHostState, DanTalkTheme.colors.singleTheme) },
        fields = fields,
        onMainButtonClick = { onIntent(InputNameStore.Intent.NavigateNext) },
        title = Pair("Регистрация", "Введите данные о себе"),
        buttonText = "Продолжить"
    )
}