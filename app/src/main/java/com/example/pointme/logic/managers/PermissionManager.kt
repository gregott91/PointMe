package com.example.pointme.logic.managers

import androidx.fragment.app.Fragment
import com.example.pointme.data.repositories.ActivityCompatRepository
import javax.inject.Inject

class PermissionManager @Inject constructor(
    private val activityCompatRepository: ActivityCompatRepository,
    private val fragment: Fragment
) {

    fun requestNeededPermissions(permissions: Array<String>, code: Int): Boolean {
        val neededPermissions: ArrayList<String> = ArrayList()
        var hasPermissions = true

        permissions.forEach { permission ->
            if (!activityCompatRepository.hasPermission(permission, fragment.activity!!)) {
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