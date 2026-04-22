package com.example.dantalk

import android.app.Application
import com.example.background.notification.FcmTokenRegistrar
import androidx.work.WorkManager
import com.example.background.notification.IncomingMessageNotifier
import com.example.background.worker.di.WorkerModule
import com.example.background.worker.util.getRequestPendingSync
import com.example.data.di.AuthModule
import com.example.data.di.DatastoreModule
import com.example.data.di.FirestoreModule
import com.example.data.di.MediaModule
import com.example.data.di.NetworkSyncModule
import com.example.data.di.StorageModule
import com.example.data.network_sync.NetworkSyncManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin

class DanTalkApplication : Application(), KoinComponent {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@DanTalkApplication)
            androidLogger()
            workManagerFactory()
            modules(listOf(
                AuthModule,
                FirestoreModule,
                DatastoreModule,
                StorageModule,
                MediaModule,
                NetworkSyncModule,
                WorkerModule
            ))
        }

        val networkSyncManager = getKoin().get<NetworkSyncManager>()
        networkSyncManager.observeNetworkChanges(CoroutineScope(Dispatchers.IO))

        val incomingMessageNotifier = getKoin().get<IncomingMessageNotifier>()
        incomingMessageNotifier.observeIncomingMessages(CoroutineScope(Dispatchers.IO))

        val fcmTokenRegistrar = getKoin().get<FcmTokenRegistrar>()
        fcmTokenRegistrar.observeUserAndRegisterToken(CoroutineScope(Dispatchers.IO))

        val workManager = getKoin().get<WorkManager>()
        workManager.enqueue(getRequestPendingSync())
    }
}
