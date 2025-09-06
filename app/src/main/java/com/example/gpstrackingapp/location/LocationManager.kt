package com.example.gpstrackingapp.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.*
import com.example.gpstrackingapp.data.model.LocationPoint
import com.example.gpstrackingapp.data.model.TrackingMetrics
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

/**
 * Manages GPS location updates and calculations
 */
@Singleton
class LocationManager @Inject constructor(
    private val context: Context
) {
    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }
    
    private var locationCallback: LocationCallback? = null
    private var locationRequest: LocationRequest? = null
    
    /**
     * Calculate distance between two points using Haversine formula
     */
    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0].toDouble()
    }
    
    /**
     * Calculate bearing between two points
     */
    fun calculateBearing(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val lat1Rad = Math.toRadians(lat1)
        val lat2Rad = Math.toRadians(lat2)
        val deltaLonRad = Math.toRadians(lon2 - lon1)
        
        val y = Math.sin(deltaLonRad) * Math.cos(lat2Rad)
        val x = Math.cos(lat1Rad) * Math.sin(lat2Rad) - Math.sin(lat1Rad) * Math.cos(lat2Rad) * Math.cos(deltaLonRad)
        
        val bearingRad = Math.atan2(y, x)
        val bearingDeg = Math.toDegrees(bearingRad)
        
        return ((bearingDeg + 360) % 360).toFloat()
    }
    
    /**
     * Check if location indicates movement (significant displacement)
     */
    fun isMoving(
        currentLat: Double,
        currentLon: Double,
        previousLat: Double,
        previousLon: Double,
        threshold: Double = 10.0 // 10 meters threshold
    ): Boolean {
        val distance = calculateDistance(currentLat, currentLon, previousLat, previousLon)
        return distance > threshold
    }
    
    /**
     * Update tracking metrics with new location
     */
    fun updateMetrics(
        metrics: TrackingMetrics,
        newLocation: LocationPoint,
        previousLocation: LocationPoint?
    ): TrackingMetrics {
        var newDistance = metrics.distance
        var newMaxSpeed = metrics.maxSpeed
        
        if (previousLocation != null) {
            val distance = calculateDistance(
                newLocation.latitude,
                newLocation.longitude,
                previousLocation.latitude,
                previousLocation.longitude
            )
            newDistance += distance
        }
        
        if (newLocation.speed > metrics.maxSpeed) {
            newMaxSpeed = newLocation.speed
        }
        
        val newLocationCount = metrics.locationCount + 1
        val newAverageSpeed = if (newLocationCount > 1) {
            (metrics.averageSpeed * (newLocationCount - 1) + newLocation.speed) / newLocationCount
        } else {
            newLocation.speed
        }
        
        return metrics.copy(
            currentSpeed = newLocation.speed,
            averageSpeed = newAverageSpeed,
            maxSpeed = newMaxSpeed,
            distance = newDistance,
            locationCount = newLocationCount
        )
    }
    
    /**
     * Create location request with specified interval
     */
    fun createLocationRequest(intervalMs: Long): LocationRequest {
        return LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, intervalMs)
            .setMinUpdateIntervalMillis(intervalMs / 2) // More frequent updates
            .setMaxUpdateDelayMillis(intervalMs)
            .setWaitForAccurateLocation(false) // Don't wait for accurate location, get updates immediately
            .build()
    }
    
    /**
     * Force a single location update
     */
    @SuppressLint("MissingPermission")
    suspend fun requestSingleLocationUpdate(): LocationPoint? {
        return try {
            val location = fusedLocationClient.lastLocation.await()
            location?.let {
                LocationPoint(
                    tripId = 0,
                    latitude = it.latitude,
                    longitude = it.longitude,
                    altitude = it.altitude,
                    speed = it.speed,
                    accuracy = it.accuracy,
                    bearing = it.bearing,
                    timestamp = Date(),
                    isMoving = it.speed > 0.5f
                )
            }
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Start location updates
     */
    @SuppressLint("MissingPermission")
    fun startLocationUpdates(intervalMs: Long): Flow<LocationPoint> = callbackFlow {
        locationRequest = createLocationRequest(intervalMs)
        
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    val locationPoint = LocationPoint(
                        tripId = 0, // Will be set by the caller
                        latitude = location.latitude,
                        longitude = location.longitude,
                        altitude = location.altitude,
                        speed = location.speed,
                        accuracy = location.accuracy,
                        bearing = location.bearing,
                        timestamp = Date(),
                        isMoving = location.speed > 0.5f // Consider moving if speed > 0.5 m/s
                    )
                    trySend(locationPoint)
                }
            }
        }
        
        fusedLocationClient.requestLocationUpdates(
            locationRequest!!,
            locationCallback!!,
            Looper.getMainLooper()
        )
        
        awaitClose {
            stopLocationUpdates()
        }
    }
    
    /**
     * Stop location updates
     */
    fun stopLocationUpdates() {
        locationCallback?.let { callback ->
            fusedLocationClient.removeLocationUpdates(callback)
            locationCallback = null
        }
        locationRequest = null
    }
    
    /**
     * Get last known location
     */
    @SuppressLint("MissingPermission")
    suspend fun getLastKnownLocation(): LocationPoint? {
        return try {
            val location = fusedLocationClient.lastLocation.await()
            location?.let {
                LocationPoint(
                    tripId = 0,
                    latitude = it.latitude,
                    longitude = it.longitude,
                    altitude = it.altitude,
                    speed = it.speed,
                    accuracy = it.accuracy,
                    bearing = it.bearing,
                    timestamp = Date(),
                    isMoving = it.speed > 0.5f
                )
            }
        } catch (e: Exception) {
            null
        }
    }
}
