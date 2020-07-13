package com.example.pointme.managers

import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.pointme.models.Coordinate
import com.example.pointme.models.entities.NavigationOperation
import com.example.pointme.models.entities.NavigationRequest
import com.example.pointme.utility.helpers.distanceFromCoordinates
import com.example.pointme.utility.helpers.getDirectionInfo
import java.time.LocalDateTime

class NavigationInitializer(
    private var operationManager: NavigationOperationManager,
    private var requestManager: NavigationRequestManager,
    private var positionManager: PositionManager
) {
    suspend fun initializeNavigation(currentLocation: Location): NavigationRequest {
        val request = requestManager.getActiveNavigation();

        initializeOperation(request, currentLocation)

        val destLat = request.latitude
        val destLon = request.longitude
        positionManager.setDestinationCoordinates(Coordinate(destLat, destLon))

        return request
    }

    private suspend fun initializeOperation(request: NavigationRequest, currentLocation: Location) {
        if (operationManager!!.getByRequest(request) == null) {
            writeNewOperation(currentLocation, request)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun writeNewOperation(currentLocation: Location, request: NavigationRequest): NavigationOperation {
        val distance = distanceFromCoordinates(
            currentLocation.latitude,
            currentLocation.longitude,
            request.latitude,
            request.longitude
        )

        var directionInfo = getDirectionInfo(
            Coordinate(currentLocation.latitude, currentLocation.longitude),
            Coordinate(request.latitude, request.longitude)
        )

        var readableDistance = directionInfo.distance + " " + directionInfo.units

        var operation = NavigationOperation(LocalDateTime.now(), readableDistance, directionInfo.direction, request.place_name, true, request.id)
        operationManager!!.insert(operation)

        return operation
    }
}