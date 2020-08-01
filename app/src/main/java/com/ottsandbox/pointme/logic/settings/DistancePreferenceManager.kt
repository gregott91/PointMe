package com.ottsandbox.pointme.logic.settings

import android.content.Context
import com.ottsandbox.pointme.R
import com.ottsandbox.pointme.models.IDistanceUnit
import com.ottsandbox.pointme.models.KilometersDistanceUnit
import com.ottsandbox.pointme.models.MilesDistanceUnit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class DistancePreferenceManager @Inject constructor(private val preferenceManager: PreferenceManager) {
    fun getDistancePreference(): IDistanceUnit {
        return when(val distancePreference = preferenceManager.distancePreference) {
            "miles" -> MilesDistanceUnit()
            "kilometers" -> KilometersDistanceUnit()
            else -> throw NotImplementedError("Unable to parse distance preference $distancePreference")
        }
    }
}