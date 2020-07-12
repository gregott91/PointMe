package com.example.pointme.managers

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.pointme.models.entities.NavigationOperation
import com.example.pointme.models.entities.NavigationRequest
import com.example.pointme.data.repositories.NavigationOperationRepository
import java.time.LocalDateTime

class NavigationOperationManager(navigationOperationRepository: NavigationOperationRepository) {
    private var mNavigationOperationRepository: NavigationOperationRepository = navigationOperationRepository

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun startNavigationOperation(distanceFeet: Double, destinationName: String, start: NavigationRequest) {
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

    suspend fun getLastSessions(limit: Int): Array<NavigationOperation> {
        return mNavigationOperationRepository.getMostRecent(limit)
    }

    suspend fun getByRequest(request: NavigationRequest): NavigationOperation {
        return mNavigationOperationRepository.getByRequestId(request!!.id)
    }

    suspend fun insert(operation: NavigationOperation) {
        return mNavigationOperationRepository.insert(operation)
    }
}