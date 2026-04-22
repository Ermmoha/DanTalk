package com.example.feature.main.chat.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.core.design.theme.DanTalkTheme
import com.example.core.ui.model.UiUserData
import com.valentinilk.shimmer.shimmer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTopBar(
    user: UiUserData?,
    onAvatarClick: () -> Unit,
    navigateBack: () -> Unit,
) {
    CenterAlignedTopAppBar(
        title = {
            if (user != null)
                Text(
                    text = user.username,
                    fontSize = 18.sp
                )
            else
                Box(
                    modifier = Modifier
                        .shimmer()
                        .size(width = 100.dp, height = 20.dp)
                        .background(DanTalkTheme.colors.hint.copy(alpha = 0.4f))
                )
        },
        modifier = Modifier.shadow(4.dp),
        navigationIcon = {
            IconButton(
                onClick = navigateBack
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = null
                )
            }
        },
        actions = {
            IconButton(
                onClick = onAvatarClick
            ) {
                if (user != null)
                    AsyncImage(
                        model = user.avatar,
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = DanTalkTheme.colors.singleTheme,
            titleContentColor = DanTalkTheme.colors.oppositeTheme,
            navigationIconContentColor = DanTalkTheme.colors.oppositeTheme
        )
    )
}