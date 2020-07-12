package com.example.pointme.data.repositories

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.pointme.models.entities.NavigationOperation

@Dao
interface NavigationOperationRepository {
    @Query("SELECT * FROM NavigationOperation WHERE id = :id")
    suspend fun getById(id: Int): NavigationOperation

    @Query("SELECT * FROM NavigationOperation WHERE request_id = :id")
    suspend fun getByRequestId(id: Int): NavigationOperation

    @Query("SELECT * FROM NavigationOperation ORDER BY id DESC LIMIT :limit")
    suspend fun getMostRecent(limit: Int): Array<NavigationOperation>

    @Insert
    suspend fun insert(operation: NavigationOperation)

    @Update
    suspend fun update(operation: NavigationOperation)
}
