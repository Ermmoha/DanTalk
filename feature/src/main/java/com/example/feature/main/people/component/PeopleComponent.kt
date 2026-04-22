package com.example.feature.main.people.component

import com.example.feature.main.people.store.PeopleStore
import kotlinx.coroutines.flow.StateFlow

interface PeopleComponent {
    val state: StateFlow<PeopleStore.State>

    fun onIntent(intent: PeopleStore.Intent)
}