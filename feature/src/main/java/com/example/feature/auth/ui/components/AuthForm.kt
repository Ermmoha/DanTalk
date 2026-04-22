package com.example.feature.auth.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.ui.components.MainButton
import com.example.core.design.theme.DanTalkTheme
import com.example.core.ui.util.InputFormField

@Composable
fun AuthForm(
    topBar: @Composable (() -> Unit)? = null,
    snackbarHost: @Composable () -> Unit,
    fields: List<InputFormField>,
    onMainButtonClick: () -> Unit,
    onBottomTextButtonClick: (() -> Unit)? = null,
    title: Pair<String, String>,
    isLoading: Boolean = false,
    buttonText: String,
    bottomButtonText: String = ""
) {
    val orientation = LocalConfiguration.current.orientation
    val isPortrait = orientation == Configuration.ORIENTATION_PORTRAIT

    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = topBar ?: {},
        snackbarHost = snackbarHost,
        containerColor = DanTalkTheme.colors.altSingleTheme
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .then(if(topBar == null) Modifier.statusBarsPadding() else Modifier)
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .then(
                    if (isPortrait) Modifier.padding(contentPadding)
                    else Modifier
                        .verticalScroll(rememberScrollState())
                        .statusBarsPadding()
                        .navigationBarsPadding()
                ),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(if (isPortrait) 120.dp else 30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Title(title)
                InputForm(
                    onSignInButtonClick = {
                        keyboardController?.hide()
                        onMainButtonClick()
                    },
                    fields = fields,
                    isLoading = isLoading,
                    buttonText = buttonText
                )
            }
            if (onBottomTextButtonClick != null)
                BottomTextButton(
                    onClick = onBottomTextButtonClick,
                    text = bottomButtonText
                )
        }
    }
}

@Composable
private fun Title(
    title: Pair<String, String>,
) {
    Column(
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title.first,
            fontSize = 28.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            color = DanTalkTheme.colors.oppositeTheme
        )
        Text(
            text = title.second,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = DanTalkTheme.colors.hint
        )
    }
}

@Composable
private fun InputForm(
    onSignInButtonClick: () -> Unit,
    fields: List<InputFormField>,
    isLoading: Boolean,
    buttonText: String,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        fields.forEach { field ->
            AuthTextField(
                value = field.value,
                onValueChange = { field.onValueChange(it) },
                modifier = Modifier.fillMaxWidth(),
                isPassword = field.isPassword,
                placeholder = field.title,
                isError = field.isError,
                isLoading = isLoading,
                keyboardOptions = field.keyboardOptions
            )
        }
        Spacer(Modifier.height(0.dp))
        MainButton(
            onClick = onSignInButtonClick,
            modifier = Modifier.fillMaxWidth(),
            buttonText = buttonText
        )
    }
}

@Composable
private fun BottomTextButton(
    onClick: () -> Unit,
    text: String
) {
    Box {
        TextButton(onClick = onClick) {
            Text(
                text = text,
                textAlign = TextAlign.Center,
                color = DanTalkTheme.colors.main
            )
        }
    }
}