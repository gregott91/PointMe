package com.example.pointme.platform.listeners

import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import com.example.pointme.logic.PositionManager
import com.example.pointme.models.Coordinate
import javax.inject.Inject

abstract class GpsLocationListener (private val positionManager: PositionManager) : LocationListener {
    override fun onLocationChanged(event: Location) {
        positionManager.setCurrentCoordinates(Coordinate(event.latitude, event.longitude))

        coordinatesChanged()
    }

    abstract fun coordinatesChanged()

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
        TODO("Not yet implemented")
    }

    override fun onProviderEnabled(p0: String?) {
        TODO("Not yet implemented")
    }

    override fun onProviderDisabled(p0: String?) {
        TODO("Not yet implemented")
    }
}