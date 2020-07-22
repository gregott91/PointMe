package com.example.pointme.data.repositories

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import javax.inject.Inject

class ActivityCompatRepository @Inject constructor() {
    fun hasPermission(permission: String, activity: Activity): Boolean {
        val result = ActivityCompat.checkSelfPermission(activity, permission)

        return result == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermissions(permissions: Array<String>, code: Int, activity: Activity){
        ActivityCompat.requestPermissions(activity, permissions, code)
    }
}