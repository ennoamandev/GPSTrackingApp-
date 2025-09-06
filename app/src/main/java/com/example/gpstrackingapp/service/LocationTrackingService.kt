package com.example.gpstrackingapp.service

import android.app.*
import android.content.Intent
import android.location.Location
import android.os.Binder
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.example.gpstrackingapp.R
import com.example.gpstrackingapp.data.model.LocationPoint
import com.example.gpstrackingapp.data.model.TrackingState
import com.example.gpstrackingapp.location.LocationManager
import com.example.gpstrackingapp.util.PreferencesManager
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.util.Date
import javax.inject.Inject

/**
 * Foreground service for continuous GPS tracking
 */
@AndroidEntryPoint
class LocationTrackingService : Service() {
    
    @Inject
    lateinit var locationManager: LocationManager
    
    @Inject
    lateinit var preferencesManager: PreferencesManager
    
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val binder = LocationTrackingBinder()
    
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback? = null
    private var currentTripId: Long = 0
    private var isTracking = false
    
    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "location_tracking_channel"
        private const val CHANNEL_NAME = "Location Tracking"
    }
    
    inner class LocationTrackingBinder : Binder() {
        fun getService(): LocationTrackingService = this@LocationTrackingService
    }
    
    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        createNotificationChannel()
    }
    
    override fun onBind(intent: Intent): IBinder {
        return binder
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            "START_TRACKING" -> {
                currentTripId = intent.getLongExtra("trip_id", 0)
                startTracking()
            }
            "STOP_TRACKING" -> {
                stopTracking()
                stopSelf()
            }
        }
        return START_STICKY
    }
    
    /**
     * Start GPS tracking
     */
    fun startTracking() {
        if (isTracking) return
        
        isTracking = true
        startForeground(NOTIFICATION_ID, createNotification())
        
        serviceScope.launch {
            val updateInterval = preferencesManager.getLocationUpdateInterval()
            startLocationUpdates(updateInterval)
        }
    }
    
    /**
     * Stop GPS tracking
     */
    fun stopTracking() {
        isTracking = false
        stopLocationUpdates()
        stopForeground(true)
    }
    
    /**
     * Start location updates
     */
    private fun startLocationUpdates(intervalMs: Long) {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, intervalMs)
            .setMinUpdateIntervalMillis(intervalMs)
            .setMaxUpdateDelayMillis(intervalMs * 2)
            .build()
        
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    onLocationUpdate(location)
                }
            }
        }
        
        try {
            fusedLocationClient?.requestLocationUpdates(
                locationRequest,
                locationCallback!!,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            // Handle permission error
        }
    }
    
    /**
     * Stop location updates
     */
    private fun stopLocationUpdates() {
        locationCallback?.let { callback ->
            fusedLocationClient?.removeLocationUpdates(callback)
            locationCallback = null
        }
    }
    
    /**
     * Handle location update
     */
    private fun onLocationUpdate(location: Location) {
        val locationPoint = LocationPoint(
            tripId = currentTripId,
            latitude = location.latitude,
            longitude = location.longitude,
            altitude = location.altitude,
            speed = location.speed,
            accuracy = location.accuracy,
            bearing = location.bearing,
            timestamp = Date(),
            isMoving = location.speed > 0.5f
        )
        
        // Update notification with current location info
        updateNotification(locationPoint)
        
        // Here you would typically save the location point to the database
        // and notify the ViewModel about the update
    }
    
    /**
     * Create notification channel for Android 8.0+
     */
    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows when GPS tracking is active"
                setShowBadge(false)
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    /**
     * Create notification for foreground service
     */
    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.tracking_notification_title))
            .setContentText(getString(R.string.tracking_notification_text))
            .setSmallIcon(R.drawable.ic_location_tracking)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
    
    /**
     * Update notification with current tracking info
     */
    private fun updateNotification(locationPoint: LocationPoint) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.tracking_notification_title))
            .setContentText("Speed: ${locationPoint.getFormattedSpeed()}")
            .setSmallIcon(R.drawable.ic_location_tracking)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
        
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
    
    /**
     * Get current tracking state
     */
    fun getTrackingState(): TrackingState {
        return if (isTracking) TrackingState.Active else TrackingState.Stopped
    }
    
    /**
     * Get current trip ID
     */
    fun getCurrentTripId(): Long = currentTripId
    
    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates()
        serviceScope.cancel()
    }
}
