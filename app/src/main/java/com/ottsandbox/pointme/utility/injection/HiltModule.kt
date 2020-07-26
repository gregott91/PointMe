package com.ottsandbox.pointme.utility.injection

import com.ottsandbox.pointme.data.databases.AppDatabase
import com.ottsandbox.pointme.data.repositories.CoordinateEntityRepository
import com.ottsandbox.pointme.data.repositories.NavigationOperationRepository
import com.ottsandbox.pointme.data.repositories.NavigationRequestRepository
import com.ottsandbox.pointme.data.repositories.PositionRepository
import com.ottsandbox.pointme.logic.DatabaseProvider
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
    fun provideAppDatabase(dbManager: DatabaseProvider): AppDatabase {
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