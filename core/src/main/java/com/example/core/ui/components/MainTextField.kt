package com.example.core.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.design.theme.DanTalkTheme

@Composable
fun MainTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = LocalTextStyle.current.copy(
        color = DanTalkTheme.colors.oppositeTheme,
    ),
    trailingIcon: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit) = {},
    isError: Boolean = false,
    colors: TextFieldColors = TextFieldDefaults.colors(
        focusedContainerColor = DanTalkTheme.colors.altSingleTheme,
        unfocusedContainerColor = DanTalkTheme.colors.altSingleTheme,
        focusedPlaceholderColor = DanTalkTheme.colors.hint,
        unfocusedPlaceholderColor = DanTalkTheme.colors.hint,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        cursorColor = DanTalkTheme.colors.main,
        errorContainerColor = DanTalkTheme.colors.altSingleTheme,
        errorIndicatorColor = Color.Transparent,
        errorCursorColor = DanTalkTheme.colors.red,
        errorPlaceholderColor = DanTalkTheme.colors.hint
    ),
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    TextField(
        value = value,
        onValueChange = { onValueChange(it) },
        modifier = if(isError)
            modifier.border(
                width = 1.dp,
                color = DanTalkTheme.colors.red.copy(alpha = 0.4f),
                shape = RoundedCornerShape(18.dp)
            )
        else modifier,
        colors = colors,
        textStyle = textStyle,
        placeholder = placeholder,
        shape = RoundedCornerShape(18.dp),
        keyboardOptions = keyboardOptions,
        trailingIcon = trailingIcon,
        isError = isError,
        maxLines = 1,
        visualTransformation = visualTransformation
    )
}