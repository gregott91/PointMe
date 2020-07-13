package com.example.pointme.managers

import com.example.pointme.models.entities.NavigationRequest
import com.example.pointme.data.repositories.NavigationStartRepository

class NavigationRequestManager(navigationStartRepository: NavigationStartRepository) {
    private var mNavigationStartRepository: NavigationStartRepository = navigationStartRepository

    suspend fun getActiveNavigation(): NavigationRequest {
        return mNavigationStartRepository.getMostRecent()
    }

    suspend fun updateActiveNavigation(placeName: String, latitude: Double, longitude: Double) {
        mNavigationStartRepository.insert(NavigationRequest(placeName, latitude, longitude))
    }
}