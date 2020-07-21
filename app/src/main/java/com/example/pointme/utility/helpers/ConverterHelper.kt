package com.example.pointme.utility.helpers

import com.example.pointme.logic.settings.DistancePreferenceManager
import kotlin.math.roundToInt

fun getShortDirectionFromAngle(angle: Double): String {
    return when ((angle / 45.0).roundToInt()) {
        0, 8 -> "N"
        1 -> "NE"
        2 -> "E"
        3 -> "SE"
        4 -> "S"
        5 -> "SW"
        6 -> "W"
        7 -> "NW"
        else -> throw IllegalArgumentException("Angle $angle cannot be converted to a short direction")
    }
}