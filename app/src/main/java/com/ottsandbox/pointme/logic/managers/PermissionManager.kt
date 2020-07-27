package com.ottsandbox.pointme.logic.managers

import android.app.Activity
import androidx.fragment.app.Fragment
import com.ottsandbox.pointme.data.repositories.ActivityCompatRepository
import javax.inject.Inject

class PermissionManager @Inject constructor(
    private val activityCompatRepository: ActivityCompatRepository,
    private val fragment: Fragment,
    private val activity: Activity
) {

    fun requestNeededPermissions(permissions: Array<String>, code: Int): Boolean {
        val neededPermissions: ArrayList<String> = ArrayList()
        var hasPermissions = true

        permissions.forEach { permission ->
            if (!activityCompatRepository.hasPermission(permission, activity)) {
                hasPermissions = false
                neededPermissions.add(permission)
            }
        }

        if (neededPermissions.any()) {
            fragment.requestPermissions(
                neededPermissions.toTypedArray(),
                code
            )
        }

        return hasPermissions

    }
}