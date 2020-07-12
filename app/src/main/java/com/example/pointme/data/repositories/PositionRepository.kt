package com.example.pointme.data.repositories

import com.example.pointme.models.Coordinate

class PositionRepository {
    var currentCoordinates: Coordinate? = null
    var destinationCoordinates: Coordinate? = null
    var currentHeading: Float? = null
}