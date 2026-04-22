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
private const val MESSAGE_SILENT_CHANNEL_ID = "message_silent_channel"
private const val MESSAGE_VIBRATE_ONLY_CHANNEL_ID = "message_vibrate_only_channel"
private const val MESSAGE_SOUND_ONLY_CHANNEL_ID = "message_sound_only_channel"

fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val imageChannel = NotificationChannel(
            IMAGE_LOAD_CHANNEL_ID,
            "Image Load",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Notifications for media uploads and downloads"
        }

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(imageChannel)
        listOf(
            createMessageChannel(MESSAGE_CHANNEL_ID, "Messages", sound = true, vibrate = true),
            createMessageChannel(
                MESSAGE_VIBRATE_ONLY_CHANNEL_ID,
                "Messages vibration",
                sound = false,
                vibrate = true
            ),
            createMessageChannel(
                MESSAGE_SOUND_ONLY_CHANNEL_ID,
                "Messages sound",
                sound = true,
                vibrate = false
            ),
            createMessageChannel(
                MESSAGE_SILENT_CHANNEL_ID,
                "Messages silent",
                sound = false,
                vibrate = false
            )
        ).forEach(manager::createNotificationChannel)
    }
}

@androidx.annotation.RequiresApi(Build.VERSION_CODES.O)
private fun createMessageChannel(
    id: String,
    name: String,
    sound: Boolean,
    vibrate: Boolean
): NotificationChannel =
    NotificationChannel(
        id,
        name,
        NotificationManager.IMPORTANCE_HIGH
    ).apply {
        description = "Notifications for incoming messages"
        enableVibration(vibrate)
        if (!sound) setSound(null, null)
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
    body: String,
    soundEnabled: Boolean = true,
    vibrationEnabled: Boolean = true
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

    val notification = NotificationCompat.Builder(
        context,
        resolveMessageChannelId(soundEnabled, vibrationEnabled)
    )
        .setSmallIcon(R.drawable.download)
        .setContentTitle(title)
        .setContentText(body)
        .setStyle(NotificationCompat.BigTextStyle().bigText(body))
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setDefaults(resolveNotificationDefaults(soundEnabled, vibrationEnabled))
        .setAutoCancel(true)
        .setContentIntent(pendingIntent)
        .apply {
            if (!vibrationEnabled) setVibrate(longArrayOf(0L))
        }
        .build()

    runCatching {
        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }
}

private fun resolveMessageChannelId(soundEnabled: Boolean, vibrationEnabled: Boolean): String =
    when {
        soundEnabled && vibrationEnabled -> MESSAGE_CHANNEL_ID
        soundEnabled -> MESSAGE_SOUND_ONLY_CHANNEL_ID
        vibrationEnabled -> MESSAGE_VIBRATE_ONLY_CHANNEL_ID
        else -> MESSAGE_SILENT_CHANNEL_ID
    }

private fun resolveNotificationDefaults(soundEnabled: Boolean, vibrationEnabled: Boolean): Int {
    var defaults = 0
    if (soundEnabled) defaults = defaults or NotificationCompat.DEFAULT_SOUND
    if (vibrationEnabled) defaults = defaults or NotificationCompat.DEFAULT_VIBRATE
    return defaults
}
