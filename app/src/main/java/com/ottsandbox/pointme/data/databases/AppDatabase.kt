package com.ottsandbox.pointme.data.databases

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ottsandbox.pointme.data.repositories.CoordinateEntityRepository
import com.ottsandbox.pointme.utility.converters.LocalDateTimeConverter
import com.ottsandbox.pointme.models.entities.NavigationOperation
import com.ottsandbox.pointme.models.entities.NavigationRequest
import com.ottsandbox.pointme.data.repositories.NavigationOperationRepository
import com.ottsandbox.pointme.data.repositories.NavigationRequestRepository
import com.ottsandbox.pointme.models.entities.CoordinateEntity

@Database(entities = [NavigationOperation::class, NavigationRequest::class, CoordinateEntity::class], version = 4)
@TypeConverters(LocalDateTimeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun navigationOperationRepository(): NavigationOperationRepository
    abstract fun navigationRequestRepository(): NavigationRequestRepository
    abstract fun coordinateEntityRepository(): CoordinateEntityRepository
}