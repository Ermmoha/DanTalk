package com.example.background.service.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.background.R

const val IMAGE_LOAD_CHANNEL_ID = "image_load_channel"
private const val MESSAGE_CHANNEL_ID = "message_channel"

fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val imageChannel = NotificationChannel(
            IMAGE_LOAD_CHANNEL_ID,
            "Image Load",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Notifications for media uploads and downloads"
        }

        val messageChannel = NotificationChannel(
            MESSAGE_CHANNEL_ID,
            "Messages",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifications for incoming messages"
        }

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(imageChannel)
        manager.createNotificationChannel(messageChannel)
    }
}

fun createNotification(
    context: Context,
    contentText: String,
    isOngoing: Boolean = true
): Notification {
    createNotificationChannel(context)

    return NotificationCompat.Builder(context, IMAGE_LOAD_CHANNEL_ID)
        .setContentTitle("Image task")
        .setContentText(contentText)
        .setSmallIcon(R.drawable.download)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setOngoing(isOngoing)
        .build()
}

fun showCompletionNotification(context: Context, text: String) {
    val notification = createNotification(context, text, false)
    val notifyManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notifyManager.notify(2, notification)
}

fun showIncomingMessageNotification(
    context: Context,
    notificationId: Int,
    title: String,
    body: String
) {
    createNotificationChannel(context)

    val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        ?: return
    val pendingIntent = PendingIntent.getActivity(
        context,
        0,
        launchIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val notification = NotificationCompat.Builder(context, MESSAGE_CHANNEL_ID)
        .setSmallIcon(R.drawable.download)
        .setContentTitle(title)
        .setContentText(body)
        .setStyle(NotificationCompat.BigTextStyle().bigText(body))
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .setContentIntent(pendingIntent)
        .build()

    runCatching {
        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }
}
