package com.ottsandbox.pointme.logic.managers

import com.ottsandbox.pointme.data.repositories.CoordinateEntityRepository
import com.ottsandbox.pointme.models.entities.NavigationRequest
import com.ottsandbox.pointme.data.repositories.NavigationRequestRepository
import com.ottsandbox.pointme.models.dtos.NavigationRequestCoordinate
import com.ottsandbox.pointme.models.entities.CoordinateEntity
import javax.inject.Inject

class NavigationRequestManager @Inject constructor(
    private val navigationRequestRepository: NavigationRequestRepository,
    private val coordinateRepo: CoordinateEntityRepository
) {
    suspend fun getActiveNavigation(): NavigationRequestCoordinate {
        return navigationRequestRepository.getMostRecent()
    }

    suspend fun updateActiveNavigation(placeName: String, latitude: Double, longitude: Double) {
        val entity = CoordinateEntity(latitude, longitude)
        val coordinateId = coordinateRepo.insert(entity)
        navigationRequestRepository.insert(NavigationRequest(placeName, coordinateId))
    }
}