package com.example.background.worker.util

import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkRequest
import com.example.background.worker.PendingSyncWorker

fun getRequestPendingSync(): WorkRequest {
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    val request = OneTimeWorkRequestBuilder<PendingSyncWorker>()
        .setConstraints(constraints)
        .build()
    return request
}