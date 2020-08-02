package com.ottsandbox.pointme.utility

import android.app.NotificationManager
import android.os.Build
import androidx.annotation.RequiresApi
import com.ottsandbox.pointme.models.Channel

@RequiresApi(Build.VERSION_CODES.N)
val DEFAULT = Channel("1", "Default", "Default channel for the PointMe app", NotificationManager.IMPORTANCE_LOW)