package com.example.gpstrackingapp.di

import android.content.Context
import com.example.gpstrackingapp.data.local.AppDatabase
import com.example.gpstrackingapp.data.local.TripDao
import com.example.gpstrackingapp.data.local.LocationPointDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for database dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }
    
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }
    
    @Provides
    @Singleton
    fun provideTripDao(database: AppDatabase): TripDao {
        return database.tripDao()
    }
    
    @Provides
    @Singleton
    fun provideLocationPointDao(database: AppDatabase): LocationPointDao {
        return database.locationPointDao()
    }
}
