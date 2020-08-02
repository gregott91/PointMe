package com.ottsandbox.pointme.logic.factories

import android.app.Activity
import android.content.Intent
import com.ottsandbox.pointme.SettingsActivity
import com.ottsandbox.pointme.models.Notification
import com.ottsandbox.pointme.models.NotificationMetadata
import com.ottsandbox.pointme.models.NotificationType
import com.ottsandbox.pointme.utility.NAVIGATION_NOTIFICATION
import javax.inject.Inject

class NotificationFactory @Inject constructor(private val activity: Activity) {
    private val notificationMappings: Map<NotificationType, NotificationMetadata> = mapOf(
        NotificationType.NAVIGATION to NAVIGATION_NOTIFICATION
    )

    fun getNotificationMetadata(type: NotificationType): NotificationMetadata {
        return notificationMappings[type] ?: error("Unable to get notification metadata from type $type")
    }

    fun <T>getNotification(title: String, text: String, intentClass: Class<T>, type: NotificationType): Notification {
        val metadata: NotificationMetadata = getNotificationMetadata(type)
        val intent = Intent(activity, intentClass)

        return Notification(title, text, intent, metadata)
    }
}