package com.example.gpstrackingapp.data.local

import androidx.room.*
import com.example.gpstrackingapp.data.model.LocationPoint
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for LocationPoint entities
 */
@Dao
interface LocationPointDao {
    
    @Query("SELECT * FROM location_points WHERE tripId = :tripId ORDER BY timestamp ASC")
    fun getLocationPointsForTrip(tripId: Long): Flow<List<LocationPoint>>
    
    @Query("SELECT * FROM location_points WHERE tripId = :tripId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastLocationPoint(tripId: Long): LocationPoint?
    
    @Query("SELECT * FROM location_points WHERE tripId = :tripId ORDER BY timestamp ASC")
    suspend fun getAllLocationPointsForTrip(tripId: Long): List<LocationPoint>
    
    @Insert
    suspend fun insertLocationPoint(locationPoint: LocationPoint): Long
    
    @Insert
    suspend fun insertLocationPoints(locationPoints: List<LocationPoint>)
    
    @Update
    suspend fun updateLocationPoint(locationPoint: LocationPoint)
    
    @Delete
    suspend fun deleteLocationPoint(locationPoint: LocationPoint)
    
    @Query("DELETE FROM location_points WHERE tripId = :tripId")
    suspend fun deleteLocationPointsForTrip(tripId: Long)
    
    @Query("SELECT COUNT(*) FROM location_points WHERE tripId = :tripId")
    suspend fun getLocationPointCountForTrip(tripId: Long): Int
    
    @Query("SELECT * FROM location_points WHERE tripId = :tripId AND isMoving = 1 ORDER BY timestamp ASC")
    suspend fun getMovingLocationPointsForTrip(tripId: Long): List<LocationPoint>
}
