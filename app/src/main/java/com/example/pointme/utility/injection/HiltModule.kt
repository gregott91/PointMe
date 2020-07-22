package com.example.pointme.utility.injection

import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.example.pointme.ArrowFragment
import com.example.pointme.LocationFragment
import com.example.pointme.data.databases.AppDatabase
import com.example.pointme.data.repositories.CoordinateEntityRepository
import com.example.pointme.data.repositories.NavigationOperationRepository
import com.example.pointme.data.repositories.NavigationRequestRepository
import com.example.pointme.data.repositories.PositionRepository
import com.example.pointme.logic.managers.DatabaseManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object HiltModule {
    //@Binds
    //abstract fun bindAnalyticsService(appDatabase: AppDatabase): AnalyticsService

    @Provides
    fun provideAppDatabase(dbManager: DatabaseManager): AppDatabase {
        return dbManager.getDatabase()
    }

    @Provides
    fun provideNavigationOperationRepository(db: AppDatabase): NavigationOperationRepository {
        return db.navigationOperationRepository()
    }

    @Provides
    fun provideNavigationRequestRepository(db: AppDatabase): NavigationRequestRepository {
        return db.navigationRequestRepository()
    }

    @Provides
    fun provideCoordinateEntityRepository(db: AppDatabase): CoordinateEntityRepository {
        return db.coordinateEntityRepository()
    }

    @Singleton
    @Provides
    fun providePositionRepository(): PositionRepository {
        return PositionRepository()
    }
}