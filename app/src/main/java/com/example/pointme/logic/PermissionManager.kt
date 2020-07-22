package com.example.pointme.logic

import android.app.Activity
import com.example.pointme.data.repositories.ActivityCompatRepository
import javax.inject.Inject

class PermissionManager @Inject constructor(private val activityCompatRepository: ActivityCompatRepository) {

    fun requestNeededPermissions(permissions: Array<String>, code: Int, activity: Activity): Boolean {
        val neededPermissions: ArrayList<String> = ArrayList()
        var hasPermissions = true

        permissions.forEach { permission ->
            if (!activityCompatRepository.hasPermission(permission, activity)) {
                hasPermissions = false
                neededPermissions.add(permission)
            }
        }

        if (neededPermissions.any()) {
            activityCompatRepository.requestPermissions(
                neededPermissions.toTypedArray(),
                code,
                activity
            )
        }

        return hasPermissions

    }
}