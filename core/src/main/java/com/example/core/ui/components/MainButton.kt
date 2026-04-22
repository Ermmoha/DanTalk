package com.example.core.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.design.theme.DanTalkTheme

@Composable
fun MainButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    buttonText: String,
    color: Color = DanTalkTheme.colors.main
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(55.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            contentColor = DanTalkTheme.colors.singleTheme
        ),
        shape = RoundedCornerShape(18.dp)
    ) {
        Text(
            text = buttonText,
            fontSize = 14.sp,
            color = Color.White
        )
    }
}