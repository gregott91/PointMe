package com.example.pointme.platform

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import com.example.pointme.platform.listeners.SensorListener
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class OrientationSensor @Inject constructor(@ActivityContext context: Context) {
    private var sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    fun registerListener(sensorListener: SensorListener) {
        sensorManager.registerListener(
            sensorListener,
            sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
            SensorManager.SENSOR_DELAY_GAME)
    }

    fun unregisterListener(sensorListener: SensorListener) {
        sensorManager.unregisterListener(sensorListener)
    }
}