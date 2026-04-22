package com.example.core.network_observer

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn

fun Context.observeConnectivityAsFlow(): Flow<ConnectionState> = callbackFlow {
    val connectivityManager =
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val callback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            trySend(connectivityManager.resolveConnectionState())
        }

        override fun onLost(network: Network) {
            trySend(connectivityManager.resolveConnectionState())
        }

        override fun onUnavailable() {
            trySend(ConnectionState.Unavailable)
        }

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            trySend(connectivityManager.resolveConnectionState(networkCapabilities))
        }
    }

    trySend(connectivityManager.resolveConnectionState())
    connectivityManager.registerDefaultNetworkCallback(callback)

    awaitClose {
        connectivityManager.unregisterNetworkCallback(callback)
    }
}
    .distinctUntilChanged()
    .flowOn(Dispatchers.IO)

private fun ConnectivityManager.resolveConnectionState(
    capabilitiesOverride: NetworkCapabilities? = null
): ConnectionState {
    val capabilities = capabilitiesOverride ?: run {
        val active = activeNetwork ?: return ConnectionState.Unavailable
        getNetworkCapabilities(active) ?: return ConnectionState.Unavailable
    }

    val hasInternetCapability = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    if (!hasInternetCapability) return ConnectionState.Unavailable

    val hasTransport = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
    if (!hasTransport) return ConnectionState.Unavailable

    return if (capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
        ConnectionState.Available
    } else {
        ConnectionState.Limited
    }
}
