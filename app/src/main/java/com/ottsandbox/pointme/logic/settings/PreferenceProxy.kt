package com.ottsandbox.pointme.logic.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.ottsandbox.pointme.R
import javax.inject.Inject

class PreferenceProxy @Inject constructor() {
    fun getStringPreference(key: String, context: Context): String? {
        var preferences = getSharedPreferences(context)
        return preferences.getString(key, context.resources.getString(R.string.default_distance_setting))
    }

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }
}