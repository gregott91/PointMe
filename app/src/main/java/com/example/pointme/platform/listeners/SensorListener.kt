package com.example.pointme.platform.listeners

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import com.example.pointme.logic.PositionManager

class SensorListener(positionManager: PositionManager, callback: () -> Unit) : SensorEventListener {
    private var mPositionManager: PositionManager = positionManager
    private var mCallback: () -> Unit = callback

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        // todo need to implement
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val currentDegree = -(event!!.values[0])

        mPositionManager.setCurrentHeading(currentDegree)

        mCallback()
    }
}