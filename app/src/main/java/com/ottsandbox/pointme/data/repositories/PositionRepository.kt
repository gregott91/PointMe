package com.ottsandbox.pointme.data.repositories

import com.ottsandbox.pointme.models.Coordinate
import javax.inject.Inject

class PositionRepository @Inject constructor() {
    var currentCoordinates: Coordinate? = null
    var currentHeading: Float? = null
}