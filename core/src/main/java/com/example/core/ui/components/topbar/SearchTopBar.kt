package com.example.core.ui.components.topbar

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.ui.components.MainTextField
import com.example.core.design.theme.DanTalkTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopBar(
    navigateBack: () -> Unit,
    query: String,
    onQueryChange: (String) -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    TopAppBar(
        title = {
            MainTextField(
                value = query,
                onValueChange = { onQueryChange(it) },
                modifier = Modifier.focusRequester(focusRequester),
                textStyle = LocalTextStyle.current.copy(
                    color = DanTalkTheme.colors.oppositeTheme,
                    fontSize = 18.sp
                ),
                placeholder = {
                    Text(
                        text = "Поиск",
                        fontSize = 18.sp
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = DanTalkTheme.colors.main
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
            )
        },
        modifier = Modifier.shadow(1.dp),
        navigationIcon = {
            IconButton(
                onClick = {
                    keyboardController?.hide()
                    navigateBack()
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = null,
                    tint = DanTalkTheme.colors.oppositeTheme
                )
            }
        },
     colors = TopAppBarDefaults.topAppBarColors(
         containerColor = DanTalkTheme.colors.extras,
         navigationIconContentColor = DanTalkTheme.colors.hint
     )
    )
}