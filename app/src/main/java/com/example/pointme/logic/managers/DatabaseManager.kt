package com.example.pointme.logic.managers

import android.content.Context
import androidx.room.Room
import com.example.pointme.DATABASE_NAME
import com.example.pointme.data.databases.AppDatabase

class DatabaseManager {
    private companion object {
        var database: AppDatabase? = null
    }

    fun getDatabase(context: Context): AppDatabase {
        if (database == null || !database!!.isOpen) {
            database = Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                DATABASE_NAME
            ).fallbackToDestructiveMigration().build()
        }

        return database!!
    }

    fun closeDatabase() {
        if (database == null || !database!!.isOpen) {
            return;
        }

        database!!.close()
    }
}