package com.example.pointme.managers

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.pointme.models.entities.NavigationOperation
import com.example.pointme.models.entities.NavigationStart
import com.example.pointme.repositories.NavigationOperationRepository
import java.time.LocalDateTime

class NavigationOperationManager(navigationOperationRepository: NavigationOperationRepository) {
    private var mNavigationOperationRepository: NavigationOperationRepository = navigationOperationRepository

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun startNavigationOperation(distanceFeet: Double, destinationName: String, start: NavigationStart) {
        val now = LocalDateTime.now()
        val navigationOperation = NavigationOperation(now, distanceFeet, destinationName, true, start.id)
        mNavigationOperationRepository.insert(navigationOperation)
    }

    suspend fun saveCompletedSession(id: Int, travelFeet: Double?) {
        var navigationOperation = mNavigationOperationRepository.getById(id)
        navigationOperation.active = false
        navigationOperation.travelFeet = travelFeet

        mNavigationOperationRepository.update(navigationOperation)
    }

    fun restorePreviouslyActiveSession() {
        // todo fill this guy in!
    }
}