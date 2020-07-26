package com.ottsandbox.pointme.data.repositories

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ottsandbox.pointme.models.entities.CoordinateEntity

@Dao
interface CoordinateEntityRepository {
    @Insert
    suspend fun insert(coordinate: CoordinateEntity): Long
}
