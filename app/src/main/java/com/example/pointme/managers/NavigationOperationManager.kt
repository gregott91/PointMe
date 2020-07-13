package com.example.pointme.managers

import com.example.pointme.models.entities.NavigationOperation
import com.example.pointme.models.entities.NavigationRequest
import com.example.pointme.data.repositories.NavigationOperationRepository

class NavigationOperationManager(navigationOperationRepository: NavigationOperationRepository) {
    private var mNavigationOperationRepository: NavigationOperationRepository = navigationOperationRepository

    suspend fun deactivateAllSessions() {
        var activeSessions = mNavigationOperationRepository.getCompleted(Int.MAX_VALUE)
        activeSessions.forEach {
            saveCompletedSession(it)
        }
    }

    suspend fun saveCompletedSession(operation: NavigationOperation) {
        operation.active = false

        mNavigationOperationRepository.update(operation)
    }

    suspend fun getLastSessions(limit: Int): Array<NavigationOperation> {
        return mNavigationOperationRepository.getCompleted(limit)
    }

    suspend fun getByRequest(request: NavigationRequest): NavigationOperation? {
        return mNavigationOperationRepository.getByRequestId(request!!.id)
    }

    suspend fun insert(operation: NavigationOperation) {
        return mNavigationOperationRepository.insert(operation)
    }
}