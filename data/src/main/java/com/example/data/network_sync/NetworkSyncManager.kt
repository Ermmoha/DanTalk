package com.example.data.network_sync

import android.content.Context
import android.util.Log
import com.example.core.network_observer.ConnectionState
import com.example.core.network_observer.observeConnectivityAsFlow
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NetworkSyncManager(
    private val context: Context,
    private val firestore: FirebaseFirestore,
) {
    fun observeNetworkChanges(scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            context.observeConnectivityAsFlow().collect { state ->
                if (state is ConnectionState.Available || state is ConnectionState.Limited) {
                    Log.d("NetworkSyncManager", "Network is connected")
                    firestore.enableNetwork()
                }
                else
                    firestore.disableNetwork()
            }
        }
    }
}
