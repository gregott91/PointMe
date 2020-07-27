package com.ottsandbox.pointme.logic.settings

import android.content.Context
import com.ottsandbox.pointme.R
import com.ottsandbox.pointme.models.IDistanceUnit
import com.ottsandbox.pointme.models.KilometersDistanceUnit
import com.ottsandbox.pointme.models.MilesDistanceUnit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class DistancePreferenceManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val proxy: PreferenceProxy
) {
    fun getDistancePreference(): IDistanceUnit {
        return when(val distancePreference = getDistancePreferenceValue()) {
            "miles" -> MilesDistanceUnit()
            "kilometers" -> KilometersDistanceUnit()
            else -> throw NotImplementedError("Unable to parse distance preference $distancePreference")
        }
    }

    private fun getDistancePreferenceValue(): String? {
        return proxy.getStringPreference(context.resources.getString(R.string.distance_settings_key), context)
    }
}