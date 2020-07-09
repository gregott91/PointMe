package com.example.pointme.databases

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.pointme.converters.LocalDateTimeConverter
import com.example.pointme.models.entities.NavigationOperation
import com.example.pointme.models.entities.NavigationStart
import com.example.pointme.repositories.NavigationOperationRepository
import com.example.pointme.repositories.NavigationStartRepository

@Database(entities = [NavigationOperation::class, NavigationStart::class], version = 1)
@TypeConverters(LocalDateTimeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun navigationOperationRepository(): NavigationOperationRepository
    abstract fun navigationStartRepository(): NavigationStartRepository
}