package com.example.pointme.models

abstract class IDistanceUnit {
    abstract val smallUnitName: String
    abstract val largeUnitName: String
    abstract val smallUnitNamePlural: String
    abstract val largeUnitNamePlural: String
    abstract val maxSmallValue: Double

    abstract fun convertToSmallValueFromFeet(feetVal: Double): Double

    fun getUserReadableDistance(feetVal: Double): Pair<String, String> {
        val finalDistanceFormatted: String
        val units: String

        val smallValue: Double = convertToSmallValueFromFeet(feetVal)

        if (smallValue > maxSmallValue) {
            val finalDistance = smallValue / maxSmallValue
            finalDistanceFormatted = "%.2f".format(smallValue / maxSmallValue)

            units = if (finalDistance == 1.0) {
                largeUnitName
            } else {
                largeUnitNamePlural
            }
        } else {
            finalDistanceFormatted = "%.0f".format(smallValue)
            units = if (smallValue == 1.0) {
                smallUnitName
            } else {
                smallUnitNamePlural
            }
        }

        return Pair(finalDistanceFormatted, units)
    }
}