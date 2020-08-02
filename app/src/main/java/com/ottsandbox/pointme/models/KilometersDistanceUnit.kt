package com.ottsandbox.pointme.models

import com.ottsandbox.pointme.models.IDistanceUnit

class KilometersDistanceUnit : IDistanceUnit() {
    override val smallUnitName: String = "meter"
    override val largeUnitName: String = "kilometer"
    override val smallUnitNamePlural: String = "meters"
    override val largeUnitNamePlural: String = "kilometers"
    override val smallestValue: Double = 1000.0
    override val twoDecimalValue: Double = 10.0
    override val oneDecimalValue: Double = 100.0

    override fun convertToSmallValueFromFeet(feetVal: Double): Double {
        return feetVal * 0.3048
    }
}