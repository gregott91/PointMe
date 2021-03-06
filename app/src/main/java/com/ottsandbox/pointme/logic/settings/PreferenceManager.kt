package com.ottsandbox.pointme.logic.settings

import android.content.Context
import com.ottsandbox.pointme.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PreferenceManager @Inject constructor(
    private val preferenceProxy: PreferenceProxy,
    @ApplicationContext private val context: Context) {
    val distancePreference: String
        get() = preferenceProxy.getPreference(
            context.resources.getString(R.string.distance_settings_key),
            context.resources.getString(R.string.default_distance_setting))

    val keepOn: Boolean
        get() = preferenceProxy.getPreference(
            context.resources.getString(R.string.keep_on_settings_key),
            context.resources.getString(R.string.default_keepon_setting).toBoolean())

    val notify: Boolean
        get() = preferenceProxy.getPreference(
            context.resources.getString(R.string.notification_settings_key),
            context.resources.getString(R.string.default_notification_setting).toBoolean())
}