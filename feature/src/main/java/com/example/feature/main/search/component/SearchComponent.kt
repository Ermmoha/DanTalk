package com.example.feature.main.search.component

import com.example.feature.main.search.store.SearchStore
import kotlinx.coroutines.flow.StateFlow

interface SearchComponent {
    val state: StateFlow<SearchStore.State>

    fun onIntent(intent: SearchStore.Intent)
}