package com.ottsandbox.pointme.logic.settings

import android.content.Context
import com.ottsandbox.pointme.R
import com.ottsandbox.pointme.models.IDistanceUnit
import com.ottsandbox.pointme.models.KilometersDistanceUnit
import com.ottsandbox.pointme.models.MilesDistanceUnit
import javax.inject.Inject

class DistancePreferenceManager @Inject constructor(private val proxy: PreferenceProxy) {
    fun getDistancePreference(context: Context): IDistanceUnit {
        return when(val distancePreference = getDistancePreferenceValue(context)) {
            "miles" -> MilesDistanceUnit()
            "kilometers" -> KilometersDistanceUnit()
            else -> throw NotImplementedError("Unable to parse distance preference $distancePreference")
        }
    }

    private fun getDistancePreferenceValue(context: Context): String? {
        return proxy.getStringPreference(context.resources.getString(R.string.distance_settings_key), context)
    }
}