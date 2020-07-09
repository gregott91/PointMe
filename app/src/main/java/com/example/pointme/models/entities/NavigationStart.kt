package com.example.pointme.models.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class NavigationStart (
    @ColumnInfo(name = "place_name") var place_name: String,
    @ColumnInfo(name = "latitude") var latitude: Double,
    @ColumnInfo(name = "longitude") var longitude: Double
) {
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}