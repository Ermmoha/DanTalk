package com.example.feature.main.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil3.compose.AsyncImage
import com.example.core.design.AppTheme
import com.example.core.design.theme.DanTalkTheme
import com.example.core.network_observer.ConnectionState
import com.example.core.network_observer.connectionState
import com.example.core.ui.components.ItemShimmer
import com.example.core.ui.components.NoInternetConnection
import com.example.core.ui.components.chat.AnimatedChatItem
import com.example.core.ui.model.UiChat
import com.example.core.ui.model.UiUserData
import com.example.feature.main.home.component.HomeComponent
import com.example.feature.main.home.store.HomeStore
import com.example.feature.main.home.ui.components.HomeNavDrawer
import com.example.feature.main.home.ui.components.HomeTopBar
import kotlinx.coroutines.launch

@Composable
fun HomeContent(
    component: HomeComponent,
    appThemeState: State<AppTheme>
) {
    val state by component.state.collectAsState()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    HomeNavDrawer(
        drawerState = drawerState,
        appTheme = appThemeState.value,
        changeTheme = { component.onIntent(HomeStore.Intent.ChangeAppTheme(it)) },
        onProfileClick = { component.onIntent(HomeStore.Intent.NavigateToProfile) },
        onPeopleClick = { component.onIntent(HomeStore.Intent.NavigateToPeople) },
        onSettingsClick = { component.onIntent(HomeStore.Intent.NavigateToSettings) },
        onInfoClick = { component.onIntent(HomeStore.Intent.NavigateToHelp) },
        onSignOutClick = { component.onIntent(HomeStore.Intent.SignOut) },
        user = state.user
    ) {
        Content(
            state = state,
            onIntent = component::onIntent,
            onMenuClick = {
                scope.launch {
                    drawerState.apply { if (isClosed) open() else close() }
                }
            }
        )
    }
}

@Composable
private fun Content(
    state: HomeStore.State,
    onIntent: (HomeStore.Intent) -> Unit,
    onMenuClick: () -> Unit,
) {
    val connectionState by connectionState()

    Scaffold(
        topBar = {
            HomeTopBar(
                onMenuClick = onMenuClick,
                onSearchClick = { onIntent(HomeStore.Intent.NavigateToSearch) }
            )
        },
        containerColor = DanTalkTheme.colors.singleTheme
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            if (state.isLoading)
                HomeShimmerContent()
            else
                HomeLazyColumn(
                    onChatClick = { onIntent(HomeStore.Intent.OpenChat(it)) },
                    onDeleteClick = { onIntent(HomeStore.Intent.DeleteChat(it)) },
                    onTogglePinClick = { onIntent(HomeStore.Intent.TogglePinChat(it)) },
                    chats = state.chats,
                )
            if (connectionState !is ConnectionState.Available)
                NoInternetConnection(
                    connectionState = connectionState,
                    modifier = Modifier.align(Alignment.BottomEnd)
                )
        }
    }
}

@Composable
private fun HomeLazyColumn(
    onChatClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    onTogglePinClick: (String) -> Unit,
    chats: List<UiChat>,
) {
    var chatForDelete by remember { mutableStateOf<UiChat?>(null) }
    var isDialogVisible by remember { mutableStateOf(false) }

    if (isDialogVisible && chatForDelete != null)
        ChatDeleteConfirmDialog(
            onConfirm = { onDeleteClick(chatForDelete?.id ?: "") },
            onDismiss = {
                isDialogVisible = false
                chatForDelete = null
            },
            user = chatForDelete?.user ?: UiUserData()
        )
    if (chats.isNotEmpty())
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .blur(if (isDialogVisible) 2.dp else 0.dp),
        ) {
            items(chats) { chat ->
                AnimatedChatItem(
                    chat = chat,
                    onChatClick = { onChatClick(chat.id) },
                    onPinIconClick = { onTogglePinClick(chat.id) },
                    onDeleteIconClick = {
                        chatForDelete = chat
                        isDialogVisible = true
                    }
                )
            }
        }
    else
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Нет активных чатов",
                color = DanTalkTheme.colors.oppositeTheme
            )
        }
}

@Composable
private fun HomeShimmerContent(
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
    ) {
        items(10) {
            ItemShimmer()
        }
    }
}

@Composable
private fun ChatDeleteConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    user: UiUserData,
) {
    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Column(
            modifier = Modifier
                .background(
                    DanTalkTheme.colors.altSingleTheme,
                    RoundedCornerShape(16.dp)
                )
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = user.avatar,
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = "Удалить чат с ${user.username}?",
                    fontSize = 16.sp,
                    color = DanTalkTheme.colors.oppositeTheme,
                    textAlign = TextAlign.Justify,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = "Это действие нельзя отменить. Вся история сообщений будет удалена.",
                color = DanTalkTheme.colors.hint,
                textAlign = TextAlign.Center
            )
            Row(
                modifier = Modifier.align(Alignment.End)
            ) {
                TextButton(
                    onClick = {
                        onDismiss()
                    }
                ) {
                    Text(
                        text = "Отмена",
                        fontSize = 14.sp,
                        color = DanTalkTheme.colors.main,
                        fontWeight = FontWeight.Bold
                    )
                }
                TextButton(
                    onClick = {
                        onConfirm()
                        onDismiss()
                    }
                ) {
                    Text(
                        text = "Подтвердить",
                        fontSize = 14.sp,
                        color = DanTalkTheme.colors.red,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
