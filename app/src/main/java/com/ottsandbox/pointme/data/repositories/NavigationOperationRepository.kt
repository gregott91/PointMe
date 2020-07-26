package com.ottsandbox.pointme.data.repositories

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.ottsandbox.pointme.models.dtos.CompletedNavigationOperation
import com.ottsandbox.pointme.models.entities.NavigationOperation
import com.ottsandbox.pointme.models.entities.NavigationRequest
import java.time.LocalDateTime

@Dao
interface NavigationOperationRepository {
    @Query("SELECT * FROM NavigationOperation WHERE id = :id")
    suspend fun getById(id: Int): NavigationOperation

    @Query("SELECT * FROM NavigationOperation WHERE request_id = :id")
    suspend fun getByRequestId(id: Long): NavigationOperation

    @Query("UPDATE NavigationOperation SET active = 0 WHERE id = :id")
    suspend fun deactivate(id: Long)

    @Query("SELECT op.id AS operationId, " +
                "req.place_name AS destinationName, " +
                "scr.latitude AS startLatitude, " +
                "scr.longitude AS startLongitude, " +
                "ecr.latitude AS endLatitude, " +
                "ecr.longitude AS endLongitude, " +
                "op.operation_date AS operationDate " +
            "FROM NavigationOperation op " +
            "INNER JOIN NavigationRequest req ON op.request_id = req.id " +
            "INNER JOIN CoordinateEntity scr ON op.start_coordinate_id = scr.id " +
            "INNER JOIN CoordinateEntity ecr ON req.dest_coordinate_id = ecr.id " +
            "WHERE op.active = 0 " +
            "ORDER BY op.id DESC LIMIT :limit")
    suspend fun getCompleted(limit: Int): Array<CompletedNavigationOperation>

    @Insert
    suspend fun insert(operation: NavigationOperation): Long
}