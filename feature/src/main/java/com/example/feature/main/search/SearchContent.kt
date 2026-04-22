package com.example.feature.main.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
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
                    .padding(top = 8.dp)
                    .imePadding()
            ) {
                item {
                    Spacer(Modifier.height(16.dp))
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Text(
                            text = "Чаты",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = DanTalkTheme.colors.oppositeTheme
                        )
                        if (state.chatsByQuery.isEmpty() &&  !state.isLoading)
                            Text(
                                text = when {
                                    state.query.isNotEmpty() -> "Ничего не найдено"
                                    else -> "Найдите чаты по username"
                                },
                                color = DanTalkTheme.colors.hint
                            )
                    }
                    Spacer(Modifier.height(6.dp))
                }
                if (state.isLoading)
                    items(10) { ItemShimmer() }
                else
                    items(state.chatsByQuery) { chat ->
                        ChatItem(
                            onChatClick = { onIntent(SearchStore.Intent.OpenChat(chat.id)) },
                            chat = chat
                        )
                    }
            }
            if (connectionState !is ConnectionState.Available)
                NoInternetConnection(
                    connectionState = connectionState,
                    modifier = Modifier.align(Alignment.BottomEnd)
                )
        }
    }
}
