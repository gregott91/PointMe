package com.ottsandbox.pointme.models.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CoordinateEntity (
    @ColumnInfo(name = "latitude") var latitude: Double,
    @ColumnInfo(name = "longitude") var longitude: Double
) {
    @PrimaryKey(autoGenerate = true) var id: Long = 0
}