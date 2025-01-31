package ru.n08i40k.polytechnic.next.service

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import ru.n08i40k.polytechnic.next.R
import ru.n08i40k.polytechnic.next.app.NotificationChannels
import ru.n08i40k.polytechnic.next.utils.app
import ru.n08i40k.polytechnic.next.worker.UpdateFCMTokenWorker


private interface MessageHandler {
    fun execute(service: FCMService)
}

private data class ScheduleUpdateData(
    val type: String,
    val replaced: Boolean,
    val etag: String
) : MessageHandler {
    constructor(message: RemoteMessage) : this(
        type = message.data["type"]
            ?: throw IllegalArgumentException("Type is missing in RemoteMessage"),
        replaced = message.data["replaced"]?.toBoolean()
            ?: throw IllegalArgumentException("Replaced is missing in RemoteMessage"),
        etag = message.data["etag"]
            ?: throw IllegalArgumentException("Etag is missing in RemoteMessage")
    )

    override fun execute(service: FCMService) {
        service.sendNotification(
            NotificationChannels.SCHEDULE_UPDATE,
            R.drawable.schedule,
            service.getString(R.string.schedule_update_title),
            service.getString(
                if (replaced)
                    R.string.schedule_update_replaced
                else
                    R.string.schedule_update_default
            ),
            etag
        )
    }
}

private data class LessonsStartData(
    val type: String
) : MessageHandler {
    constructor(message: RemoteMessage) : this(
        type = message.data["type"]
            ?: throw IllegalArgumentException("Type is missing in RemoteMessage")
    )

    override fun execute(service: FCMService) {
        DayViewService.start(service.app)
    }
}

private data class AppUpdateData(
    val type: String,
    val version: String,
    val downloadLink: String
) : MessageHandler {
    constructor(message: RemoteMessage) : this(
        type = message.data["type"]
            ?: throw IllegalArgumentException("Type is missing in RemoteMessage"),
        version = message.data["version"]
            ?: throw IllegalArgumentException("Version is missing in RemoteMessage"),
        downloadLink = message.data["downloadLink"]
            ?: throw IllegalArgumentException("DownloadLink is missing in RemoteMessage")
    )

    override fun execute(service: FCMService) {
        service.sendNotification(
            NotificationChannels.APP_UPDATE,
            R.drawable.download,
            service.getString(R.string.app_update_title, version),
            service.getString(R.string.app_update_description),
            version,
            Intent(Intent.ACTION_VIEW, Uri.parse(downloadLink))
        )
    }
}

class FCMService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)

        UpdateFCMTokenWorker.schedule(this, token)
    }


    fun sendNotification(
        channel: String,
        @DrawableRes iconId: Int,
        title: String,
        contentText: String,
        id: Any?,
        intent: Intent? = null
    ) {
        val pendingIntent: PendingIntent? =
            if (intent != null)
                PendingIntent.getActivity(this, 0, intent.apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }, PendingIntent.FLAG_IMMUTABLE)
            else
                null

        val notification = NotificationCompat
            .Builder(applicationContext, channel)
            .setSmallIcon(iconId)
            .setContentTitle(title)
            .setContentText(contentText)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    this@FCMService,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return@with
            }

            notify(id.hashCode(), notification)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val type = message.data["type"]

        when (type) {
            "schedule-update" -> ScheduleUpdateData(message)
            "lessons-start"   -> LessonsStartData(message)
            "app-update"      -> AppUpdateData(message)
            else              -> null
        }?.execute(this)

        super.onMessageReceived(message)
    }
}