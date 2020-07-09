package com.example.pointme.managers

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.pointme.models.entities.NavigationOperation
import com.example.pointme.models.entities.NavigationStart
import com.example.pointme.repositories.NavigationOperationRepository
import com.example.pointme.repositories.NavigationStartRepository
import java.time.LocalDateTime

class NavigationStartManager(navigationStartRepository: NavigationStartRepository) {
    private var mNavigationStartRepository: NavigationStartRepository = navigationStartRepository

    suspend fun getActiveNavigation(): NavigationStart {
        return mNavigationStartRepository.getMostRecent()
    }

    suspend fun updateActiveNavigation(placeName: String, latitude: Double, longitude: Double) {
        mNavigationStartRepository.insert(NavigationStart(placeName, latitude, longitude))
    }
}