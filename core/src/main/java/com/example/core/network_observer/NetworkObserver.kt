package com.example.core.network_observer

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import java.net.HttpURLConnection
import java.net.URL

fun Context.observeConnectivityAsFlow(): Flow<ConnectionState> = callbackFlow {
    val connectivityManager =
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    fun sendResolvedState(capabilities: NetworkCapabilities? = null) {
        launch {
            trySend(connectivityManager.resolveConnectionState(capabilities))
        }
    }

    val callback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            sendResolvedState()
        }

        override fun onLost(network: Network) {
            sendResolvedState()
        }

        override fun onUnavailable() {
            trySend(ConnectionState.Unavailable)
        }

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            sendResolvedState(networkCapabilities)
        }
    }

    sendResolvedState()
    connectivityManager.registerDefaultNetworkCallback(callback)

    awaitClose {
        connectivityManager.unregisterNetworkCallback(callback)
    }
}
    .distinctUntilChanged()
    .flowOn(Dispatchers.IO)

private suspend fun ConnectivityManager.resolveConnectionState(
    capabilitiesOverride: NetworkCapabilities? = null
): ConnectionState {
    val active = activeNetwork ?: return ConnectionState.Unavailable
    val capabilities = capabilitiesOverride ?: run {
        getNetworkCapabilities(active) ?: return ConnectionState.Unavailable
    }

    val hasInternetCapability = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    if (!hasInternetCapability) return ConnectionState.Unavailable

    val hasTransport = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
    if (!hasTransport) return ConnectionState.Unavailable

    return if (hasRealInternetAccess(active)) {
        ConnectionState.Available
    } else {
        ConnectionState.Limited
    }
}

private suspend fun hasRealInternetAccess(network: Network): Boolean = withContext(Dispatchers.IO) {
    val endpoints = listOf(
        "https://clients3.google.com/generate_204" to HttpURLConnection.HTTP_NO_CONTENT,
        "https://connectivitycheck.gstatic.com/generate_204" to HttpURLConnection.HTTP_NO_CONTENT,
        "https://www.cloudflare.com/cdn-cgi/trace" to HttpURLConnection.HTTP_OK
    )

    endpoints.any { (url, expectedCode) ->
        runCatching {
            val connection = network.openConnection(URL(url)) as HttpURLConnection
            connection.instanceFollowRedirects = false
            connection.connectTimeout = 1_500
            connection.readTimeout = 1_500
            connection.useCaches = false
            connection.connect()
            val isReachable = connection.responseCode == expectedCode
            connection.disconnect()
            isReachable
        }.getOrDefault(false)
    }
}
