package com.example.pointme.logic.managers

import com.example.pointme.models.entities.NavigationOperation
import com.example.pointme.data.repositories.NavigationOperationRepository

class NavigationOperationManager(private val navigationOperationRepository: NavigationOperationRepository) {
    suspend fun getLastSessions(limit: Int): Array<NavigationOperationRepository.CompletedNavigationOperation> {
        return navigationOperationRepository.getCompleted(limit)
    }

    suspend fun getByRequestId(requestId: Long): NavigationOperation? {
        return navigationOperationRepository.getByRequestId(requestId)
    }

    suspend fun insert(operation: NavigationOperation): Long {
        return navigationOperationRepository.insert(operation)
    }

    suspend fun deactivate(operation: NavigationOperation) {
        navigationOperationRepository.deactivate(operation.id)
    }
}