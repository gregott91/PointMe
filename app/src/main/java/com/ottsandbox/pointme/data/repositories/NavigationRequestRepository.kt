package com.ottsandbox.pointme.data.repositories

import androidx.navigation.Navigation
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ottsandbox.pointme.models.dtos.NavigationRequestCoordinate
import com.ottsandbox.pointme.models.entities.NavigationRequest

@Dao
interface NavigationRequestRepository {
    @Query("SELECT req.id AS requestId, req.place_name AS placeName, cor.longitude, cor.latitude " +
            "FROM NavigationRequest req " +
            "INNER JOIN CoordinateEntity cor ON req.dest_coordinate_id = cor.id " +
            "ORDER BY req.id DESC LIMIT 1")
    suspend fun getMostRecent(): NavigationRequestCoordinate

    @Insert
    suspend fun insert(start: NavigationRequest)
}
