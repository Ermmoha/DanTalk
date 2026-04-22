package com.example.feature.main.search

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.design.theme.DanTalkTheme
import com.example.core.network_observer.ConnectionState
import com.example.core.network_observer.connectionState
import com.example.core.ui.components.ItemShimmer
import com.example.core.ui.components.NoInternetConnection
import com.example.core.ui.components.chat.ChatItem
import com.example.core.ui.components.topbar.SearchTopBar
import com.example.feature.main.search.component.SearchComponent
import com.example.feature.main.search.store.SearchStore

@Composable
fun SearchContent(
    component: SearchComponent,
) {
    val state by component.state.collectAsState()

    Content(
        state = state,
        onIntent = component::onIntent
    )
}

@Composable
private fun Content(
    state: SearchStore.State,
    onIntent: (SearchStore.Intent) -> Unit,
) {
    val connectionState by connectionState()

    Scaffold(
        topBar = {
            SearchTopBar(
                navigateBack = { onIntent(SearchStore.Intent.NavigateBack) },
                query = state.query,
                onQueryChange = { onIntent(SearchStore.Intent.OnQueryChange(it)) }
            )
        },
        containerColor = DanTalkTheme.colors.singleTheme
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 10.dp)
                    .imePadding()
            ) {
                item {
                    SearchHeader(
                        count = state.chatsByQuery.size,
                        query = state.query,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                when {
                    state.isLoading -> items(10) { ItemShimmer() }

                    state.chatsByQuery.isEmpty() -> item {
                        EmptyChatsContent(
                            title = if (state.query.isEmpty()) {
                                "Искать чаты"
                            } else {
                                "Ничего не найдено"
                            },
                            subtitle = if (state.query.isEmpty()) {
                                "Введите имя пользователя для того, чтобы найти чат"
                            } else {
                                "Попробуйте ввести другое имя пользователя"
                            }
                        )
                    }

                    else -> items(state.chatsByQuery) { chat ->
                        ChatItem(
                            onChatClick = { onIntent(SearchStore.Intent.OpenChat(chat.id)) },
                            chat = chat
                        )
                    }
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
private fun SearchHeader(
    count: Int,
    query: String,
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
                imageVector = Icons.Outlined.Chat,
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
                    text = "Чаты",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DanTalkTheme.colors.oppositeTheme
                )
                Text(
                    text = when {
                        query.isBlank() -> "Поиск по имени пользователя"
                        count == 1 -> "Найден 1 чат"
                        count in 2..4 -> "Найдено $count чата"
                        else -> "Найдено $count чатов"
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
private fun EmptyChatsContent(
    title: String,
    subtitle: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 88.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.Search,
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
