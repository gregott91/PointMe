package com.example.pointme.data.databases

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.pointme.utility.converters.LocalDateTimeConverter
import com.example.pointme.models.entities.NavigationOperation
import com.example.pointme.models.entities.NavigationRequest
import com.example.pointme.data.repositories.NavigationOperationRepository
import com.example.pointme.data.repositories.NavigationStartRepository

@Database(entities = [NavigationOperation::class, NavigationRequest::class], version = 2)
@TypeConverters(LocalDateTimeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun navigationOperationRepository(): NavigationOperationRepository
    abstract fun navigationStartRepository(): NavigationStartRepository
}