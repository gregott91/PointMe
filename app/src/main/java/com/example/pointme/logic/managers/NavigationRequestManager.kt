package com.example.pointme.logic.managers

import com.example.pointme.data.repositories.CoordinateEntityRepository
import com.example.pointme.models.entities.NavigationRequest
import com.example.pointme.data.repositories.NavigationRequestRepository
import com.example.pointme.models.dtos.NavigationRequestCoordinate
import com.example.pointme.models.entities.CoordinateEntity

class NavigationRequestManager(private val navigationStartRepository: NavigationRequestRepository, private val coordinateRepo: CoordinateEntityRepository) {

    suspend fun getActiveNavigation(): NavigationRequestCoordinate {
        return navigationStartRepository.getMostRecent()
    }

    suspend fun updateActiveNavigation(placeName: String, latitude: Double, longitude: Double) {
        val entity = CoordinateEntity(latitude, longitude)
        val coordinateId = coordinateRepo.insert(entity)
        navigationStartRepository.insert(NavigationRequest(placeName, coordinateId))
    }
}