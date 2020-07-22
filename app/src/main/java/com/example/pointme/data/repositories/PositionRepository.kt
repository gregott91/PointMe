package com.example.pointme.data.repositories

import com.example.pointme.models.Coordinate
import javax.inject.Inject

class PositionRepository @Inject constructor() {
    var currentCoordinates: Coordinate? = null
    var currentHeading: Float? = null
}