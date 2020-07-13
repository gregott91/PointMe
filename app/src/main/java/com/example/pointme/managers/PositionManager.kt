package com.example.pointme.managers

import com.example.pointme.models.Location
import com.example.pointme.models.Coordinate
import com.example.pointme.data.repositories.PositionRepository

class PositionManager(positionRepository: PositionRepository) {
    private var mPositionRepository: PositionRepository = positionRepository
    private var lock = Object()

    fun getCurrentLocation(): Location {
        synchronized(lock) {
            return Location(
                mPositionRepository.currentCoordinates,
                mPositionRepository.currentHeading
            )
        }
    }

    fun setCurrentHeading(heading: Float) {
        synchronized(lock) {
            mPositionRepository.currentHeading = heading
        }
    }

    fun setCurrentCoordinates(coordinate: Coordinate) {
        synchronized(lock) {
            mPositionRepository.currentCoordinates = coordinate
        }
    }

    fun setDestinationCoordinates(coordinate: Coordinate) {
        mPositionRepository.destinationCoordinates = coordinate
    }

    fun getDestinationCoordinates(): Coordinate? {
        return mPositionRepository.destinationCoordinates
    }
}