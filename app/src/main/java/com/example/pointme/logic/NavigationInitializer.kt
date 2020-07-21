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

class NavigationInitializer(
    private var operationManager: NavigationOperationManager,
    private var requestManager: NavigationRequestManager,
    private var positionManager: PositionManager,
    private var coordinateEntityRepository: CoordinateEntityRepository
) {
    suspend fun initializeNavigation(currentLocation: Location): Pair<NavigationRequestCoordinate, NavigationOperation> {
        val request = requestManager.getActiveNavigation();

        val operation = writeNewOperationIfNeeded(request, currentLocation)
        positionManager.setDestinationCoordinates(Coordinate(request.latitude, request.longitude))

        return Pair(request, operation)
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
        val coordinateId = coordinateEntityRepository.insert(CoordinateEntity(currentLocation.longitude, currentLocation.latitude))
        val operation = NavigationOperation(LocalDateTime.now(), true, request.requestId, coordinateId)
        val operationId = operationManager.insert(operation)

        operation.id = operationId

        return operation
    }
}