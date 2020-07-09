package com.example.pointme.managers

import android.content.Context
import androidx.room.Room
import com.example.pointme.DATABASE_NAME
import com.example.pointme.databases.AppDatabase

class DatabaseManager {
    private companion object {
        var database: AppDatabase? = null
    }

    fun getDatabase(context: Context): AppDatabase {
        if (database == null) {
            database = Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                DATABASE_NAME
            ).build()
        }

        return database!!
    }
}