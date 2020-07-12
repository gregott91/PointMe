package com.example.pointme.data.repositories

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.pointme.models.entities.NavigationRequest

@Dao
interface NavigationStartRepository {
    @Query("SELECT * FROM NavigationRequest ORDER BY id DESC LIMIT 1")
    suspend fun getMostRecent(): NavigationRequest

    @Insert
    suspend fun insert(start: NavigationRequest)
}
