package com.example.core.ui.components.snackbar

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.core.design.theme.DanTalkTheme

@Composable
fun CustomSnackbarHost(
    snackbarHostState: SnackbarHostState,
    containerColor: Color = DanTalkTheme.colors.altSingleTheme
) {
    SnackbarHost(
        hostState = snackbarHostState,
    ) { snackbarData ->
        Snackbar(
            modifier = Modifier.padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            containerColor = containerColor,
            contentColor = DanTalkTheme.colors.oppositeTheme
        ) {
            Text(text = snackbarData.visuals.message)
        }
    }
}