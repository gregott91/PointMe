package com.example.pointme.utility.helpers

import com.example.pointme.models.Coordinate
import com.example.pointme.models.DirectionInfo
import com.example.pointme.models.DistanceInfo
import com.example.pointme.models.IDistanceUnit

fun getDirectionInfo(current: Coordinate, destination: Coordinate): DirectionInfo {
    val angle = current.angleTo(destination)
    val direction = getShortDirectionFromAngle(angle)

    return DirectionInfo(direction, angle)
}

fun getDistanceInfo(current: Coordinate, destination: Coordinate, distanceUnit: IDistanceUnit): DistanceInfo {
    val distance = current.feetFrom(destination)
    val (finalDistance, units) = distanceUnit.getUserReadableDistance(distance)

    return DistanceInfo(finalDistance, units)
}