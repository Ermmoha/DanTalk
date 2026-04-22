package com.example.core.ui.components.topbar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.core.design.theme.DanTalkTheme
import com.example.core.ui.components.IconButtonWithElevation
import kotlinx.coroutines.delay

@Composable
fun ExtraTopBar(
    navigateBack: () -> Unit,
    color: Color = DanTalkTheme.colors.oppositeTheme,
) {
    IconButton(
        onClick = navigateBack,
        modifier = Modifier
            .statusBarsPadding()
            .padding(horizontal = 8.dp)
            .size(50.dp),
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = Color.Transparent,
            contentColor = color
        )
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun ExtraTopBar(
    actionsIcon: ImageVector,
    actions: () -> Unit,
    navigateBack: () -> Unit,
    color: Color = DanTalkTheme.colors.oppositeTheme,
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(200)
        visible = true
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn() + slideInHorizontally()
        ) {
            IconButtonWithElevation(
                onClick = navigateBack,
                modifier = Modifier.size(50.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = color
                ),
                icon = Icons.AutoMirrored.Outlined.ArrowBack,
                contentModifier = Modifier.size(24.dp)
            )
        }
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn() + slideInHorizontally { it / 2 }
        ) {
            IconButtonWithElevation(
                onClick = actions,
                modifier = Modifier.size(50.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = color
                ),
                icon = actionsIcon,
                contentModifier = Modifier.size(24.dp)
            )
        }
    }
}