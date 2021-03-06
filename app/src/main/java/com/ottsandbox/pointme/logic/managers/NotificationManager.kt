package com.ottsandbox.pointme.logic.managers

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavDeepLinkBuilder
import com.ottsandbox.pointme.R
import com.ottsandbox.pointme.logic.factories.ChannelFactory
import com.ottsandbox.pointme.logic.factories.NotificationFactory
import com.ottsandbox.pointme.models.Channel
import com.ottsandbox.pointme.models.ChannelType
import com.ottsandbox.pointme.models.Notification
import com.ottsandbox.pointme.models.NotificationType
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
class NotificationManager @Inject constructor(
    @ActivityContext private val context: Context,
    private val activity: Activity,
    private val notificationFactory: NotificationFactory,
    private val channelFactory: ChannelFactory
) {
    private var notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun <T>makeNotification(title: String, text: String, intentClass: Class<T>, fragmentId: Int, notificationType: NotificationType, channelType: ChannelType) where T : Activity {
        val notification = notificationFactory.getNotification(title, text, intentClass, fragmentId, notificationType)
        val channel = channelFactory.getChannel(channelType)

        makeNotification<T>(notification, channel)
    }

    private fun <T>makeNotification(notification: Notification<T>, channel: Channel) where T : Activity {
        val builder = createBuilder(notification, channel)
        createChannel(channel)

        with(NotificationManagerCompat.from(context)) {
            notify(notification.metadata.id, builder.build())
        }
    }

    fun removeNotification(notificationType: NotificationType) {
        val metadata = notificationFactory.getNotificationMetadata(notificationType)
        notificationManager.cancel(metadata.id)
    }

    private fun <T>createBuilder(notification: Notification<T>, channel: Channel): NotificationCompat.Builder where T : Activity {
        val pendingIntent: PendingIntent = NavDeepLinkBuilder(context)
            .setComponentName(notification.intentClass)
            .setGraph(R.navigation.nav_graph)
            .setDestination(notification.fragmentId)
            .createPendingIntent()

        return NotificationCompat.Builder(activity, channel.id)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(notification.title)
            .setContentText(notification.text)
            .setPriority(notification.metadata.priority)
            .setContentIntent(pendingIntent)
            .setOngoing(notification.metadata.ongoing)
    }

    private fun createChannel(channel: Channel) {
        val notificationChannel = NotificationChannel(channel.id, channel.name, channel.importance).apply {
            description = channel.description
        }

        notificationManager.createNotificationChannel(notificationChannel)
    }
}