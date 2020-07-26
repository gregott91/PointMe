package com.example.pointme.logic

import android.content.Context
import androidx.room.Room
import com.example.pointme.DATABASE_NAME
import com.example.pointme.data.databases.AppDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class DatabaseProvider @Inject constructor(@ApplicationContext private val context: Context){
    private companion object {
        var database: AppDatabase? = null
    }

    fun getDatabase(): AppDatabase {
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