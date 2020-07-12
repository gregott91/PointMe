package com.example.pointme.models.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(foreignKeys = [ForeignKey(entity = NavigationRequest::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("request_id"),
    onDelete = ForeignKey.NO_ACTION)])
data class NavigationOperation (
    @ColumnInfo(name = "operation_date") var operationDate: LocalDateTime,
    @ColumnInfo(name = "distance_feet") var distanceFeet: Double,
    @ColumnInfo(name = "destination_name") var destinationName: String,
    @ColumnInfo(name = "active") var active: Boolean?,
    @ColumnInfo(name = "request_id") var requestId: Int
) {
    @PrimaryKey(autoGenerate = true) var id: Int = 0
    @ColumnInfo(name = "travel_feet") var travelFeet: Double? = null
}
