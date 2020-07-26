package com.ottsandbox.pointme.platform.listeners

import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import com.ottsandbox.pointme.models.Coordinate

abstract class GpsLocationListener : LocationListener {
    override fun onLocationChanged(event: Location) {
        coordinatesChanged(Coordinate(event.latitude, event.longitude))
    }

    abstract fun coordinatesChanged(location: Coordinate)

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