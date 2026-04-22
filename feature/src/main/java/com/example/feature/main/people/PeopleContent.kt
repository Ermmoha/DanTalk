package com.example.feature.main.people

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.PersonSearch
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.core.design.theme.DanTalkTheme
import com.example.core.network_observer.ConnectionState
import com.example.core.network_observer.connectionState
import com.example.core.ui.components.ItemShimmer
import com.example.core.ui.components.NoInternetConnection
import com.example.core.ui.components.UserDialogInfo
import com.example.core.ui.components.topbar.SearchTopBar
import com.example.core.ui.model.UiUserData
import com.example.feature.main.people.component.PeopleComponent
import com.example.feature.main.people.store.PeopleStore

@Composable
fun PeopleContent(
    component: PeopleComponent,
) {
    val state by component.state.collectAsState()

    Content(
        state = state,
        onIntent = component::onIntent
    )
}

@Composable
private fun Content(
    state: PeopleStore.State,
    onIntent: (PeopleStore.Intent) -> Unit,
) {
    var dialogUserData: UiUserData? by remember { mutableStateOf(null) }
    var isDialogVisible by remember { mutableStateOf(false) }

    val connectionState by connectionState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            SearchTopBar(
                navigateBack = { onIntent(PeopleStore.Intent.NavigateBack) },
                query = state.query,
                onQueryChange = { onIntent(PeopleStore.Intent.OnQueryChange(it)) },
            )
        },
        containerColor = DanTalkTheme.colors.singleTheme
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            when {
                state.isLoading -> ShimmerContent()

                state.query.isNotEmpty() && state.usersByQuery.isEmpty() ->
                    EmptyUsersContent(
                        title = "Ничего не найдено",
                        subtitle = "Попробуйте ввести другое имя пользователя"
                    )

                state.query.isEmpty() ->
                    EmptyUsersContent(
                        title = "Найти пользователей",
                        subtitle = "Введите имя пользователя для поиска"
                    )

                else -> {
                    if (isDialogVisible) {
                        UserDialogInfo(
                            onDismissRequest = { isDialogVisible = false },
                            actionButtonContent = {
                                Text(
                                    text = "Send message",
                                    fontSize = 16.sp
                                )
                            },
                            onActionButtonClick = {
                                isDialogVisible = false
                                onIntent(PeopleStore.Intent.OpenChat(dialogUserData!!.id))
                            },
                            onDownloadButtonClick = {
                                onIntent(
                                    PeopleStore.Intent.DownloadImage(
                                        context = context,
                                        url = dialogUserData?.avatar ?: ""
                                    )
                                )
                            },
                            user = dialogUserData ?: UiUserData()
                        )
                    }
                    PeopleLazyColumn(
                        users = state.usersByQuery,
                        onUserClick = {
                            dialogUserData = it
                            isDialogVisible = true
                        },
                        onMessageSendClick = { onIntent(PeopleStore.Intent.OpenChat(it)) },
                        modifier = Modifier.blur(if (isDialogVisible) 3.dp else 0.dp)
                    )
                }
            }
            if (connectionState !is ConnectionState.Available) {
                NoInternetConnection(
                    connectionState = connectionState,
                    modifier = Modifier.align(Alignment.BottomEnd)
                )
            }
        }
    }
}

@Composable
private fun PeopleLazyColumn(
    users: List<UiUserData>,
    onUserClick: (UiUserData) -> Unit,
    onMessageSendClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 10.dp)
            .imePadding()
    ) {
        item {
            PeopleHeader(
                count = users.size,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }
        items(users) { user ->
            UserItem(
                onUserClick = { onUserClick(user) },
                onMessageSendClick = { onMessageSendClick(user.id) },
                userData = user
            )
        }
    }
}

@Composable
private fun PeopleHeader(
    count: Int,
    modifier: Modifier = Modifier
) {
    Column {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DanTalkTheme.colors.altSingleTheme)
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.PersonSearch,
                contentDescription = null,
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(DanTalkTheme.colors.singleTheme)
                    .padding(7.dp),
                tint = DanTalkTheme.colors.main
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "Пользователи",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DanTalkTheme.colors.oppositeTheme
                )
                Text(
                    text = when {
                        count == 1 -> "Найден 1 пользователь"
                        count in 2..4 -> "Найдено $count пользователя"
                        else -> "Найдено $count пользователей"
                    },
                    fontSize = 13.sp,
                    color = DanTalkTheme.colors.hint
                )
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun UserItem(
    modifier: Modifier = Modifier,
    onUserClick: () -> Unit,
    onMessageSendClick: () -> Unit,
    userData: UiUserData,
) {
    Column(
        modifier = modifier
            .background(DanTalkTheme.colors.singleTheme)
            .clickable { onUserClick() },
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Spacer(Modifier.height(2.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = userData.avatar,
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(DanTalkTheme.colors.spacer, CircleShape),
                    contentScale = ContentScale.Crop
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(
                        text = userData.firstname.ifBlank { userData.username },
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Medium,
                        color = DanTalkTheme.colors.oppositeTheme
                    )
                    Text(
                        text = "@${userData.username}",
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Medium,
                        color = DanTalkTheme.colors.hint
                    )
                }
            }
            IconButton(
                onClick = onMessageSendClick,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(DanTalkTheme.colors.altSingleTheme),
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = DanTalkTheme.colors.main
                ),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Mail,
                    contentDescription = null
                )
            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 12.dp),
            color = DanTalkTheme.colors.spacer
        )
    }
}

@Composable
private fun EmptyUsersContent(
    title: String,
    subtitle: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.PersonSearch,
            contentDescription = null,
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(DanTalkTheme.colors.altSingleTheme)
                .padding(12.dp),
            tint = DanTalkTheme.colors.main
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            color = DanTalkTheme.colors.oppositeTheme
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = subtitle,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            color = DanTalkTheme.colors.hint
        )
    }
}

@Composable
private fun ShimmerContent(
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 8.dp)
            .imePadding()
    ) {
        items(10) {
            ItemShimmer()
        }
    }
}
