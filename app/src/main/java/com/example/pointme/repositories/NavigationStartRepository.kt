package com.example.pointme.repositories

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.pointme.models.entities.NavigationStart

@Dao
interface NavigationStartRepository {
    @Query("SELECT * FROM NavigationStart ORDER BY id DESC LIMIT 1")
    suspend fun getMostRecent(): NavigationStart

    @Insert
    suspend fun insert(start: NavigationStart)
}
