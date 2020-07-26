package com.ottsandbox.pointme.models.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(foreignKeys = [
    ForeignKey(entity = CoordinateEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("dest_coordinate_id"),
        onDelete = ForeignKey.NO_ACTION)
])
data class NavigationRequest (
    @ColumnInfo(name = "place_name") var place_name: String,
    @ColumnInfo(name = "dest_coordinate_id") var destinationCoordinateId: Long
) {
    @PrimaryKey(autoGenerate = true) var id: Long = 0
}