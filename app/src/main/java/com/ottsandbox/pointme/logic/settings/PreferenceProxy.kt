package com.ottsandbox.pointme.logic.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.ottsandbox.pointme.R
import javax.inject.Inject

class PreferenceProxy @Inject constructor() {
    fun getStringPreference(key: String, defaultValue: String, context: Context): String {
        var preferences = getSharedPreferences(context)
        return preferences.getString(key, defaultValue)!!
    }

    fun getBooleanPreference(key: String, defaultValue: Boolean, context: Context): Boolean {
        var preferences = getSharedPreferences(context)
        return preferences.getBoolean(key, defaultValue)!!
    }

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }
}