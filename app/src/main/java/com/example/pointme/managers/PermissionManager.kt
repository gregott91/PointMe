package com.example.pointme.managers

import android.app.Activity
import com.example.pointme.data.repositories.ActivityCompatRepository

class PermissionManager(activityCompatRepository: ActivityCompatRepository) {
    private var mActivityCompatRepository: ActivityCompatRepository = activityCompatRepository

    fun requestNeededPermissions(permissions: Array<String>, code: Int, activity: Activity): Boolean {
        var neededPermissions: ArrayList<String> = ArrayList()
        var hasPermissions = true

        permissions.forEach { permission ->
            if (!mActivityCompatRepository.hasPermission(permission, activity)) {
                hasPermissions = false
                neededPermissions.add(permission)
            }
        }

        if (neededPermissions.any()) {
            mActivityCompatRepository.requestPermissions(
                neededPermissions.toTypedArray(),
                code,
                activity
            )
        }

        return hasPermissions

    }
}