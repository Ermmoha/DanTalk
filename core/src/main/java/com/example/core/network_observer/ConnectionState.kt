package com.example.core.network_observer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalContext

sealed class ConnectionState {
    data object Checking : ConnectionState()
    data object Available : ConnectionState()
    data object Unavailable : ConnectionState()
}

@Composable
fun connectionState(): State<ConnectionState> {
    val context = LocalContext.current

    return produceState<ConnectionState>(initialValue = ConnectionState.Checking) {
        context.observeConnectivityAsFlow().collect { value = it }
    }
}
