package com.example.pointme.utility.helpers

import com.example.pointme.models.Coordinate
import com.example.pointme.models.DirectionInfo
import com.example.pointme.models.IDistanceUnit

fun getDirectionInfo(current: Coordinate, destination: Coordinate, distanceUnit: IDistanceUnit): DirectionInfo {
    val distance = current.feetFrom(destination)
    val angle = current.angleTo(destination)

    val (finalDistance, units) = distanceUnit.getUserReadableDistance(distance)
    val direction = getShortDirectionFromAngle(angle)

    return DirectionInfo(finalDistance, units, direction, angle)
}