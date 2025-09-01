package com.example.gpstrackingapp.data.local

import androidx.room.*
import com.example.gpstrackingapp.data.model.Trip
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Trip entities
 */
@Dao
interface TripDao {
    
    @Query("SELECT * FROM trips ORDER BY startTime DESC")
    fun getAllTrips(): Flow<List<Trip>>
    
    @Query("SELECT * FROM trips WHERE isCompleted = 1 ORDER BY startTime DESC")
    fun getCompletedTrips(): Flow<List<Trip>>
    
    @Query("SELECT * FROM trips WHERE id = :tripId")
    suspend fun getTripById(tripId: Long): Trip?
    
    @Query("SELECT * FROM trips WHERE isCompleted = 0 ORDER BY startTime DESC LIMIT 1")
    suspend fun getCurrentTrip(): Trip?
    
    @Insert
    suspend fun insertTrip(trip: Trip): Long
    
    @Update
    suspend fun updateTrip(trip: Trip)
    
    @Delete
    suspend fun deleteTrip(trip: Trip)
    
    @Query("DELETE FROM trips WHERE id = :tripId")
    suspend fun deleteTripById(tripId: Long)
    
    @Query("SELECT COUNT(*) FROM trips")
    suspend fun getTripCount(): Int
    
    @Query("SELECT SUM(totalDistance) FROM trips WHERE isCompleted = 1")
    suspend fun getTotalDistance(): Double?
    
    @Query("SELECT AVG(averageSpeed) FROM trips WHERE isCompleted = 1")
    suspend fun getAverageSpeed(): Double?
}
