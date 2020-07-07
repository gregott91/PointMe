package com.example.pointme.helpers

import kotlin.math.acos
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

fun angleFromCoordinate(
    startLat: Double,
    startLon: Double,
    endLat: Double,
    endLon: Double
): Double {
    val dLon = endLon - startLon
    val y = sin(dLon) * cos(endLat)
    val x = cos(startLat) * sin(endLat) - (sin(startLat) * cos(endLat) * cos(dLon))
    var brng = atan2(y, x)
    brng = Math.toDegrees(brng)
    brng = (brng + 360) % 360
    brng = 360 - brng
    return brng
}

fun distanceFromCoordinates(
    startLat: Double,
    startLon: Double,
    endLat: Double,
    endLon: Double
): Double {
    val theta = startLon - endLon
    var dist =
        sin(Math.toRadians(startLat)) * sin(
            Math.toRadians(endLat)
        ) + cos(Math.toRadians(startLat)) * cos(
            Math.toRadians(
                endLat
            )
        ) * cos(Math.toRadians(theta))
    dist = acos(dist)
    dist = Math.toDegrees(dist)
    dist *= 60 * 1.1515

    return dist * 5280
}