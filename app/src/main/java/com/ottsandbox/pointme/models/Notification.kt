package com.ottsandbox.pointme.models

class Notification<T>(
    val title: String,
    val text: String,
    val intentClass: Class<T>,
    val fragmentId: Int,
    val metadata: NotificationMetadata) {}