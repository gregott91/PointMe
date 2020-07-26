package com.example.pointme.platform.listeners

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener

abstract class SensorListener : SensorEventListener {

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        // todo need to implement
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val currentDegree = -(event!!.values[0])

        degreeChanged(currentDegree)
    }

    abstract fun degreeChanged(currentHeading: Float)
}