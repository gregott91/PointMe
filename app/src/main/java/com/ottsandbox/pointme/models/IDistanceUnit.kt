package com.ottsandbox.pointme.models

abstract class IDistanceUnit {
    abstract val smallUnitName: String
    abstract val largeUnitName: String
    abstract val smallUnitNamePlural: String
    abstract val largeUnitNamePlural: String
    abstract val smallestValue: Double
    abstract val twoDecimalValue: Double
    abstract val oneDecimalValue: Double

    abstract fun convertToSmallValueFromFeet(feetVal: Double): Double

    fun getUserReadableDistance(feetVal: Double): Pair<String, String> {
        val finalDistanceFormatted: String
        val units: String

        val smallValue: Double = convertToSmallValueFromFeet(feetVal)

        if (smallValue > smallestValue) {
            val finalDistance = smallValue / smallestValue

            finalDistanceFormatted = getFormatString(finalDistance).format(finalDistance)

            units = getUnits(finalDistance, largeUnitName, largeUnitNamePlural)
        } else {
            finalDistanceFormatted = "%.0f".format(smallValue)
            units = getUnits(smallValue, smallUnitName, smallUnitNamePlural)
        }

        return Pair(finalDistanceFormatted, units)
    }

    private fun getUnits(distance: Double, singleName: String, pluralName: String): String {
        return if (distance == 1.0) {
            singleName
        } else {
            pluralName
        }
    }

    private fun getFormatString(distance: Double): String {
        return when {
            distance < twoDecimalValue -> { "%.2f" }
            distance < oneDecimalValue -> { "%.1f" }
            else -> { "%.0f" }
        }
    }
}