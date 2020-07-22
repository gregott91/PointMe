package com.example.pointme.data.databases

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.pointme.data.repositories.CoordinateEntityRepository
import com.example.pointme.utility.converters.LocalDateTimeConverter
import com.example.pointme.models.entities.NavigationOperation
import com.example.pointme.models.entities.NavigationRequest
import com.example.pointme.data.repositories.NavigationOperationRepository
import com.example.pointme.data.repositories.NavigationRequestRepository
import com.example.pointme.models.entities.CoordinateEntity

@Database(entities = [NavigationOperation::class, NavigationRequest::class, CoordinateEntity::class], version = 4)
@TypeConverters(LocalDateTimeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun navigationOperationRepository(): NavigationOperationRepository
    abstract fun navigationRequestRepository(): NavigationRequestRepository
    abstract fun coordinateEntityRepository(): CoordinateEntityRepository
}