package com.ottsandbox.pointme.models.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(foreignKeys = [
    ForeignKey(entity = NavigationRequest::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("request_id"),
        onDelete = ForeignKey.NO_ACTION),
    ForeignKey(entity = CoordinateEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("start_coordinate_id"),
        onDelete = ForeignKey.NO_ACTION)
    ])
data class NavigationOperation (
    @ColumnInfo(name = "operation_date") var operationDate: LocalDateTime,
    @ColumnInfo(name = "active") var active: Boolean,
    @ColumnInfo(name = "request_id") var requestId: Long,
    @ColumnInfo(name = "start_coordinate_id") var startCoordinateId: Long
) {
    @PrimaryKey(autoGenerate = true) var id: Long = 0
}
