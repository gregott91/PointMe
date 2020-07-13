package com.example.pointme.utility.helpers

import com.example.pointme.models.Coordinate
import com.example.pointme.models.DirectionInfo

fun getDirectionInfo(current: Coordinate, destination: Coordinate): DirectionInfo {
    val distance = distanceFromCoordinates(
        current.latitude,
        current.longitude,
        destination.latitude,
        destination.longitude
    )

    val angle = angleFromCoordinate(
        current.latitude,
        current.longitude,
        destination.latitude,
        destination.longitude
    )

    val (finalDistance, units) = getUserReadableDistance(distance)
    val direction = getShortDirectionFromAngle(angle)

    return DirectionInfo(finalDistance, units, direction, angle)
}