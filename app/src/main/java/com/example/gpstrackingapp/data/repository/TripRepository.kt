package com.example.gpstrackingapp.data.repository

import com.example.gpstrackingapp.data.local.TripDao
import com.example.gpstrackingapp.data.local.LocationPointDao
import com.example.gpstrackingapp.data.model.Trip
import com.example.gpstrackingapp.data.model.LocationPoint
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing trip and location data
 */
@Singleton
class TripRepository @Inject constructor(
    private val tripDao: TripDao,
    private val locationPointDao: LocationPointDao
) {
    
    // Trip operations
    fun getAllTrips(): Flow<List<Trip>> = tripDao.getAllTrips()
    
    fun getCompletedTrips(): Flow<List<Trip>> = tripDao.getCompletedTrips()
    
    suspend fun getTripById(tripId: Long): Trip? = tripDao.getTripById(tripId)
    
    suspend fun getCurrentTrip(): Trip? = tripDao.getCurrentTrip()
    
    suspend fun insertTrip(trip: Trip): Long = tripDao.insertTrip(trip)
    
    suspend fun updateTrip(trip: Trip) = tripDao.updateTrip(trip)
    
    suspend fun deleteTrip(trip: Trip) = tripDao.deleteTrip(trip)
    
    suspend fun deleteTripById(tripId: Long) = tripDao.deleteTripById(tripId)
    
    suspend fun getTripCount(): Int = tripDao.getTripCount()
    
    suspend fun getTotalDistance(): Double? = tripDao.getTotalDistance()
    
    suspend fun getAverageSpeed(): Double? = tripDao.getAverageSpeed()
    
    // Location point operations
    fun getLocationPointsForTrip(tripId: Long): Flow<List<LocationPoint>> = 
        locationPointDao.getLocationPointsForTrip(tripId)
    
    suspend fun getLastLocationPoint(tripId: Long): LocationPoint? = 
        locationPointDao.getLastLocationPoint(tripId)
    
    suspend fun getAllLocationPointsForTrip(tripId: Long): List<LocationPoint> = 
        locationPointDao.getAllLocationPointsForTrip(tripId)
    
    suspend fun insertLocationPoint(locationPoint: LocationPoint): Long = 
        locationPointDao.insertLocationPoint(locationPoint)
    
    suspend fun insertLocationPoints(locationPoints: List<LocationPoint>) = 
        locationPointDao.insertLocationPoints(locationPoints)
    
    suspend fun updateLocationPoint(locationPoint: LocationPoint) = 
        locationPointDao.updateLocationPoint(locationPoint)
    
    suspend fun deleteLocationPoint(locationPoint: LocationPoint) = 
        locationPointDao.deleteLocationPoint(locationPoint)
    
    suspend fun deleteLocationPointsForTrip(tripId: Long) = 
        locationPointDao.deleteLocationPointsForTrip(tripId)
    
    suspend fun getLocationPointCountForTrip(tripId: Long): Int = 
        locationPointDao.getLocationPointCountForTrip(tripId)
    
    suspend fun getMovingLocationPointsForTrip(tripId: Long): List<LocationPoint> = 
        locationPointDao.getMovingLocationPointsForTrip(tripId)
}
