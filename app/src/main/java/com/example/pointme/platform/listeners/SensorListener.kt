package com.example.pointme.platform.listeners

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import com.example.pointme.logic.PositionManager
import javax.inject.Inject

abstract class SensorListener (private val positionManager: PositionManager) : SensorEventListener {

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        // todo need to implement
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val currentDegree = -(event!!.values[0])
        positionManager.setCurrentHeading(currentDegree)

        degreeChanged()
    }

    abstract fun degreeChanged()
}