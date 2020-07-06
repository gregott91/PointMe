package com.example.pointme.listeners

import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import com.example.pointme.managers.PositionManager
import com.example.pointme.models.Coordinate

class GpsLocationListener(positionManager: PositionManager, callback: () -> Unit) : LocationListener {
    private var mPositionManager: PositionManager = positionManager
    private var mCallback: () -> Unit = callback

    override fun onLocationChanged(event: Location) {
        mPositionManager.setCurrentCoordinates(Coordinate(event!!.latitude, event!!.longitude))

        mCallback()
    }

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