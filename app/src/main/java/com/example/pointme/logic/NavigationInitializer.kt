package com.example.pointme.logic

import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.pointme.data.repositories.CoordinateEntityRepository
import com.example.pointme.data.repositories.NavigationRequestRepository
import com.example.pointme.logic.managers.NavigationOperationManager
import com.example.pointme.logic.managers.NavigationRequestManager
import com.example.pointme.models.Coordinate
import com.example.pointme.models.dtos.NavigationRequestCoordinate
import com.example.pointme.models.entities.CoordinateEntity
import com.example.pointme.models.entities.NavigationOperation
import java.time.LocalDateTime
import javax.inject.Inject

class NavigationInitializer @Inject constructor(
    private val operationManager: NavigationOperationManager,
    private val coordinateEntityRepository: CoordinateEntityRepository
) {
    suspend fun initializeNavigation(request: NavigationRequestCoordinate, currentLocation: Location): NavigationOperation {
        val operation = writeNewOperationIfNeeded(request, currentLocation)

        return operation
    }

    private suspend fun writeNewOperationIfNeeded(request: NavigationRequestCoordinate, currentLocation: Location): NavigationOperation {
        var operation = operationManager.getByRequestId(request.requestId)

        if (operation == null) {
            operation = writeNewOperation(currentLocation, request)
        }

        return operation
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun writeNewOperation(currentLocation: Location, request: NavigationRequestCoordinate): NavigationOperation {
        val coordinateId = coordinateEntityRepository.insert(CoordinateEntity(currentLocation.latitude, currentLocation.longitude))
        val operation = NavigationOperation(LocalDateTime.now(), true, request.requestId, coordinateId)
        val operationId = operationManager.insert(operation)

        operation.id = operationId

        return operation
    }
}