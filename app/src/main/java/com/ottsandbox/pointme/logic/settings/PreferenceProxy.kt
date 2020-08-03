package com.ottsandbox.pointme.logic.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.ottsandbox.pointme.R
import com.ottsandbox.pointme.models.NotificationType
import dagger.Binds
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PreferenceProxy @Inject constructor(@ApplicationContext private val context: Context) {
    inline fun <reified T>getPreference(key: String, defaultValue: T? = null): T {
        val preferences = getSharedPreferences()

        return when (T::class) {
            String::class -> preferences.getString(key, (defaultValue ?: "") as String)!!
            Boolean::class -> preferences.getBoolean(key, (defaultValue ?: true) as Boolean)
            else -> error("Unable to get shared preference for type ${T::class}")
        } as T
    }

    inline fun <reified T>registerPreferenceChangedListener(settingKey: String, crossinline onChange: (newValue: T) -> Unit) {
        getSharedPreferences()
            .registerOnSharedPreferenceChangeListener { _, key ->
                if (key == settingKey) {
                    val preference = getPreference<T>(settingKey)
                    onChange(preference)
                }
            }
    }

    fun getSharedPreferences(): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }
}