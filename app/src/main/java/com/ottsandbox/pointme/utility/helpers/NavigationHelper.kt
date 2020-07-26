package com.ottsandbox.pointme.utility.helpers

import com.ottsandbox.pointme.models.Coordinate
import com.ottsandbox.pointme.models.DirectionInfo
import com.ottsandbox.pointme.models.DistanceInfo
import com.ottsandbox.pointme.models.IDistanceUnit

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