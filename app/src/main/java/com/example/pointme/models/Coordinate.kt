package com.example.pointme.models

import kotlin.math.acos
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class Coordinate(var latitude: Double, var longitude: Double) {
    fun feetFrom(
        end: Coordinate
    ): Double {
        val theta = longitude - end.longitude
        var dist =
            sin(Math.toRadians(latitude)) * sin(
                Math.toRadians(end.latitude)
            ) + cos(Math.toRadians(latitude)) * cos(
                Math.toRadians(
                    end.latitude
                )
            ) * cos(Math.toRadians(theta))
        dist = acos(dist)
        dist = Math.toDegrees(dist)
        dist *= 60 * 1.1515

        return dist
    }

    fun angleTo(
        end: Coordinate
    ): Double {
        val dLon = end.longitude - longitude
        val y = sin(dLon) * cos(end.latitude)
        val x = cos(latitude) * sin(end.latitude) - (sin(latitude) * cos(end.latitude) * cos(dLon))
        var brng = atan2(y, x)
        brng = Math.toDegrees(brng)
        brng = (brng + 360) % 360
        brng = 360 - brng
        return brng
    }
}