package com.example.feature.main.home.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedIconToggleButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.core.design.AppTheme
import com.example.core.design.theme.DanTalkTheme
import com.example.core.ui.model.UiUserData

@Composable
fun HomeNavDrawer(
    drawerState: DrawerState,
    appTheme: AppTheme,
    changeTheme: (AppTheme) -> Unit,
    onProfileClick: () -> Unit,
    onPeopleClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onInfoClick: () -> Unit,
    onSignOutClick: () -> Unit,
    user: UiUserData,
    content: @Composable () -> Unit,
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Column(
                modifier = Modifier
                    .padding(end = 60.dp)
                    .background(DanTalkTheme.colors.extras)
                    .fillMaxHeight()
            ) {
                NavDrawerHeader(
                    user = user,
                    appTheme = appTheme,
                    onChangeTheme = { changeTheme(it) }
                )
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .navigationBarsPadding(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        NavDrawerItem(
                            onClick = onProfileClick,
                            text = "Профиль",
                            icon = Icons.Outlined.AccountCircle
                        )
                        NavDrawerItem(
                            onClick = onPeopleClick,
                            text = "Люди",
                            icon = Icons.Outlined.People
                        )
                        HorizontalDivider()
                        NavDrawerItem(
                            onClick = onSettingsClick,
                            text = "Настройки",
                            icon = Icons.Outlined.Settings
                        )
                        NavDrawerItem(
                            onClick = onInfoClick,
                            text = "Справка",
                            icon = Icons.Outlined.Info
                        )
                    }
                    NavDrawerItem(
                        onClick = onSignOutClick,
                        text = "Выйти из аккаунта",
                        icon = Icons.AutoMirrored.Outlined.Logout
                    )
                }
            }
        },
        gesturesEnabled = true,
        content = content
    )
}

@Composable
private fun NavDrawerHeader(
    user: UiUserData,
    appTheme: AppTheme,
    onChangeTheme: (AppTheme) -> Unit,
) {
    val isSystemInDarkTheme = appTheme == AppTheme.DARK || appTheme == AppTheme.SYSTEM && isSystemInDarkTheme()
    Column(
        modifier = Modifier
            .background(
                if (isSystemInDarkTheme)
                    Color(21, 21, 21, 255)
                else
                    DanTalkTheme.colors.main
            )
            .statusBarsPadding()
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AsyncImage(
                model = user.avatar,
                contentDescription = null,
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            IconButton(
                onClick = {
                    when (appTheme) {
                        AppTheme.DARK -> onChangeTheme(AppTheme.LIGHT)
                        AppTheme.LIGHT -> onChangeTheme(AppTheme.DARK)
                        AppTheme.SYSTEM -> if (isSystemInDarkTheme)
                            onChangeTheme(AppTheme.LIGHT) else onChangeTheme(AppTheme.DARK)
                    }
                },
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = if (isSystemInDarkTheme) Icons.Filled.LightMode else Icons.Default.DarkMode,
                    contentDescription = null
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = user.firstname,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = user.username,
            fontWeight = FontWeight.Medium,
            color = if(isSystemInDarkTheme) DanTalkTheme.colors.hint else Color.White
        )
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun NavDrawerItem(
    onClick: () -> Unit,
    text: String,
    icon: ImageVector,
) {
    NavigationDrawerItem(
        label = {
            Text(
                text = text,
                fontWeight = FontWeight.Medium,
                color = DanTalkTheme.colors.oppositeTheme
            )
        },
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = DanTalkTheme.colors.hint
            )
        },
        selected = false,
        shape = RectangleShape,
        onClick = onClick
    )
}