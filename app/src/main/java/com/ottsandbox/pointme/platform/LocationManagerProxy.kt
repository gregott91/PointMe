package com.ottsandbox.pointme.platform

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import com.ottsandbox.pointme.models.Coordinate
import com.ottsandbox.pointme.platform.listeners.GpsLocationListener
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

@SuppressLint("MissingPermission")
class LocationManagerProxy @Inject constructor(@ActivityContext context: Context) {
    private var locationManager: LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    fun requestLocationUpdates(locationListener: GpsLocationListener) {
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            250,
            0.5f,
            locationListener
        )
    }

    fun getLastKnownCoordinates(): Coordinate {
        val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)!!
        return Coordinate(location.latitude, location.longitude)
    }
}