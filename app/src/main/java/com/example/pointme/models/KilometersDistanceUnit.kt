package com.example.pointme.models

import com.example.pointme.models.IDistanceUnit

class KilometersDistanceUnit : IDistanceUnit() {
    override val smallUnitName: String = "meter"
    override val largeUnitName: String = "kilometer"
    override val smallUnitNamePlural: String = "meters"
    override val largeUnitNamePlural: String = "kilometers"
    override val maxSmallValue: Double = 1000.0

    override fun convertToSmallValueFromFeet(feetVal: Double): Double {
        return feetVal * 0.3048
    }
}