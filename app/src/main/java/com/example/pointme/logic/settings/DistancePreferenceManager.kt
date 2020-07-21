package com.example.pointme.logic.settings

import android.content.Context
import com.example.pointme.R
import com.example.pointme.models.IDistanceUnit
import com.example.pointme.models.KilometersDistanceUnit
import com.example.pointme.models.MilesDistanceUnit

class DistancePreferenceManager(private val proxy: PreferenceProxy) {
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