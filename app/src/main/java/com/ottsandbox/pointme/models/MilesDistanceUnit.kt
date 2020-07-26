package com.ottsandbox.pointme.models

import com.ottsandbox.pointme.models.IDistanceUnit

class MilesDistanceUnit : IDistanceUnit() {
    override val smallUnitName: String = "foot"
    override val largeUnitName: String = "mile"
    override val smallUnitNamePlural: String = "feet"
    override val largeUnitNamePlural: String = "miles"
    override val maxSmallValue: Double = 5280.0

    override fun convertToSmallValueFromFeet(feetVal: Double): Double {
        return feetVal
    }
}