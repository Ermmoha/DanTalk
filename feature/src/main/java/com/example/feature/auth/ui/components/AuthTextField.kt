package com.example.feature.auth.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.design.theme.DanTalkTheme
import com.example.core.ui.components.MainTextField

@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    placeholder: String,
    isError: Boolean,
    isLoading: Boolean,
    keyboardOptions: KeyboardOptions,
) {
    var isVisible by remember { mutableStateOf(false) }

    MainTextField(
        value = value,
        onValueChange = { onValueChange(it) },
        modifier = modifier,
        textStyle = LocalTextStyle.current.copy(
            fontSize = 16.sp,
            color = DanTalkTheme.colors.oppositeTheme
        ),
        trailingIcon = {
            when {
                isPassword && !isLoading -> IconButton(
                    onClick = { isVisible = !isVisible },
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = DanTalkTheme.colors.hint
                    )
                ) {
                    Icon(
                        imageVector = if (!isVisible)
                            Icons.Outlined.Visibility
                        else
                            Icons.Outlined.VisibilityOff,
                        contentDescription = null
                    )
                }

                isLoading -> CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = DanTalkTheme.colors.main
                )
            }
        },
        placeholder = {
            Text(
                text = placeholder,
                fontSize = 14.sp
            )
        },
        isError = isError,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = DanTalkTheme.colors.singleTheme,
            unfocusedContainerColor = DanTalkTheme.colors.singleTheme,
            focusedPlaceholderColor = DanTalkTheme.colors.hint,
            unfocusedPlaceholderColor = DanTalkTheme.colors.hint,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = DanTalkTheme.colors.main,
            errorContainerColor = DanTalkTheme.colors.singleTheme,
            errorIndicatorColor = Color.Transparent,
            errorCursorColor = DanTalkTheme.colors.red,
            errorPlaceholderColor = DanTalkTheme.colors.hint
        ),
        keyboardOptions = keyboardOptions,
        visualTransformation = if (isPassword && !isVisible)
            PasswordVisualTransformation()
        else
            VisualTransformation.None
    )
}