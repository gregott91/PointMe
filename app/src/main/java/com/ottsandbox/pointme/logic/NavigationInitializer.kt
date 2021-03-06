package com.ottsandbox.pointme.logic

import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import com.ottsandbox.pointme.data.repositories.CoordinateEntityRepository
import com.ottsandbox.pointme.data.repositories.NavigationRequestRepository
import com.ottsandbox.pointme.logic.managers.NavigationOperationManager
import com.ottsandbox.pointme.logic.managers.NavigationRequestManager
import com.ottsandbox.pointme.models.Coordinate
import com.ottsandbox.pointme.models.dtos.NavigationRequestCoordinate
import com.ottsandbox.pointme.models.entities.CoordinateEntity
import com.ottsandbox.pointme.models.entities.NavigationOperation
import java.time.LocalDateTime
import javax.inject.Inject

class NavigationInitializer @Inject constructor(
    private val operationManager: NavigationOperationManager,
    private val coordinateEntityRepository: CoordinateEntityRepository
) {
    suspend fun initializeNavigation(request: NavigationRequestCoordinate, currentLocation: Coordinate): NavigationOperation {
        var operation = operationManager.getByRequestId(request.requestId)

        if (operation == null) {
            operation = writeNewOperation(currentLocation, request)
        }

        return operation
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun writeNewOperation(currentLocation: Coordinate, request: NavigationRequestCoordinate): NavigationOperation {
        val coordinateId = coordinateEntityRepository.insert(CoordinateEntity(currentLocation.latitude, currentLocation.longitude))
        val operation = NavigationOperation(LocalDateTime.now(), true, request.requestId, coordinateId)
        val operationId = operationManager.insert(operation)

        operation.id = operationId

        return operation
    }
}