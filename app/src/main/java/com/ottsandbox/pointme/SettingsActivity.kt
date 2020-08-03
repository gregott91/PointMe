package com.ottsandbox.pointme

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.ottsandbox.pointme.logic.managers.NotificationManager
import com.ottsandbox.pointme.logic.settings.PreferenceProxy
import com.ottsandbox.pointme.models.NotificationType
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {
    @Inject lateinit var notificationManager: NotificationManager
    @Inject lateinit var preferenceProxy: PreferenceProxy

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
        actionBar?.setDisplayHomeAsUpEnabled(true)

        preferenceProxy.registerPreferenceChangedListener<Boolean>(getString(R.string.notification_settings_key)) { notify ->
            if (!notify) {
                notificationManager.removeNotification(NotificationType.NAVIGATION)
            }
        }
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
    }
}