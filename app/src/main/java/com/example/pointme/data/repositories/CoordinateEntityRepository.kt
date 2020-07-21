package com.example.pointme.data.repositories

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.pointme.models.entities.CoordinateEntity

@Dao
interface CoordinateEntityRepository {
    @Insert
    suspend fun insert(coordinate: CoordinateEntity): Long
}
