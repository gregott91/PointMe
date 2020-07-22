package com.example.pointme.logic

import com.example.pointme.models.Location
import com.example.pointme.models.Coordinate
import com.example.pointme.data.repositories.PositionRepository
import javax.inject.Inject

class PositionManager @Inject constructor(private val positionRepository: PositionRepository) {

    private var lock: Any = Object()

    fun getCurrentLocation(): Location {
        synchronized(lock) {
            return Location(
                positionRepository.currentCoordinates,
                positionRepository.currentHeading
            )
        }
    }

    fun setCurrentHeading(heading: Float) {
        synchronized(lock) {
            positionRepository.currentHeading = heading
        }
    }

    fun setCurrentCoordinates(coordinate: Coordinate) {
        synchronized(lock) {
            positionRepository.currentCoordinates = coordinate
        }
    }
}