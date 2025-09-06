package com.example.gpstrackingapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gpstrackingapp.data.model.*
import com.example.gpstrackingapp.data.repository.TripRepository
import com.example.gpstrackingapp.location.LocationManager
import com.example.gpstrackingapp.util.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

/**
 * ViewModel for managing GPS tracking functionality
 */
@HiltViewModel
class TrackingViewModel @Inject constructor(
    private val tripRepository: TripRepository,
    private val locationManager: LocationManager,
    private val preferencesManager: PreferencesManager
) : ViewModel() {
    
    private val _trackingState = MutableStateFlow<TrackingState>(TrackingState.Stopped)
    val trackingState: StateFlow<TrackingState> = _trackingState.asStateFlow()
    
    private val _trackingMetrics = MutableStateFlow(TrackingMetrics())
    val trackingMetrics: StateFlow<TrackingMetrics> = _trackingMetrics.asStateFlow()
    
    private val _currentLocation = MutableStateFlow<LocationPoint?>(null)
    val currentLocation: StateFlow<LocationPoint?> = _currentLocation.asStateFlow()
    
    private val _isTracking = MutableStateFlow(false)
    val isTracking: StateFlow<Boolean> = _isTracking.asStateFlow()
    
    private var currentTripId: Long = 0
    private var previousLocation: LocationPoint? = null
    private var trackingStartTime: Long = 0
    private var locationUpdateJob: kotlinx.coroutines.Job? = null
    
    init {
        viewModelScope.launch {
            // Check if there's an ongoing trip
            val currentTrip = tripRepository.getCurrentTrip()
            if (currentTrip != null) {
                currentTripId = currentTrip.id
                _trackingState.value = TrackingState.Paused
                _isTracking.value = true
                loadTripMetrics(currentTrip)
            }
        }
    }
    
    /**
     * Start GPS tracking
     */
    fun startTracking() {
        println("startTracking called, current state: ${_trackingState.value}")
        if (_trackingState.value == TrackingState.Stopped) {
            viewModelScope.launch {
                println("Starting new tracking session...")
                _trackingState.value = TrackingState.Starting
                
                // Create new trip
                val trip = Trip(
                    startTime = Date(),
                    isCompleted = false
                )
                currentTripId = tripRepository.insertTrip(trip)
                println("Created trip with ID: $currentTripId")
                
                // Start location updates
                startLocationUpdates()
                
                _trackingState.value = TrackingState.Active
                _isTracking.value = true
                trackingStartTime = System.currentTimeMillis()
                println("Tracking started successfully")
            }
        } else if (_trackingState.value == TrackingState.Paused) {
            println("Resuming paused tracking...")
            resumeTracking()
        }
    }
    
    /**
     * Pause GPS tracking
     */
    fun pauseTracking() {
        if (_trackingState.value == TrackingState.Active) {
            _trackingState.value = TrackingState.Paused
            locationUpdateJob?.cancel()
        }
    }
    
    /**
     * Resume GPS tracking
     */
    fun resumeTracking() {
        if (_trackingState.value == TrackingState.Paused) {
            _trackingState.value = TrackingState.Active
            startLocationUpdates()
        }
    }
    
    /**
     * Stop GPS tracking
     */
    fun stopTracking() {
        viewModelScope.launch {
            _trackingState.value = TrackingState.Stopping
            
            // Stop location updates
            locationUpdateJob?.cancel()
            locationManager.stopLocationUpdates()
            
            // Complete the trip
            if (currentTripId > 0) {
                val currentTrip = tripRepository.getTripById(currentTripId)
                currentTrip?.let { trip ->
                    val endTime = Date()
                    val duration = System.currentTimeMillis() - trackingStartTime
                    
                    val completedTrip = trip.copy(
                        endTime = endTime,
                        duration = duration,
                        totalDistance = _trackingMetrics.value.distance,
                        averageSpeed = _trackingMetrics.value.averageSpeed,
                        maxSpeed = _trackingMetrics.value.maxSpeed,
                        isCompleted = true
                    )
                    
                    tripRepository.updateTrip(completedTrip)
                }
            }
            
            // Reset state
            _trackingState.value = TrackingState.Stopped
            _isTracking.value = false
            _trackingMetrics.value = TrackingMetrics()
            _currentLocation.value = null
            currentTripId = 0
            previousLocation = null
            trackingStartTime = 0
        }
    }
    
    /**
     * Load initial location when permissions are granted
     */
    fun loadInitialLocation() {
        viewModelScope.launch {
            try {
                println("Loading initial location...")
                // Try to get last known location first
                var location = locationManager.getLastKnownLocation()
                
                // If no last known location, try to request a single update
                if (location == null) {
                    println("No last known location, requesting single update...")
                    location = locationManager.requestSingleLocationUpdate()
                }
                
                if (location != null) {
                    println("Initial location loaded: ${location.latitude}, ${location.longitude}")
                    _currentLocation.value = location
                } else {
                    println("No location available after trying both methods")
                }
            } catch (e: Exception) {
                // Handle location loading error
                println("Error loading initial location: ${e.message}")
                e.printStackTrace()
            }
        }
    }
    
    /**
     * Start location updates
     */
    private fun startLocationUpdates() {
        locationUpdateJob = viewModelScope.launch {
            try {
                val updateInterval = preferencesManager.getLocationUpdateInterval()
                println("Starting location updates with interval: ${updateInterval}ms")
                
                locationManager.startLocationUpdates(updateInterval).collect { locationPoint ->
                    println("Received location update: ${locationPoint.latitude}, ${locationPoint.longitude}")
                    
                    // Update current location
                    _currentLocation.value = locationPoint.copy(tripId = currentTripId)
                    
                    // Save location point to database
                    if (currentTripId > 0) {
                        tripRepository.insertLocationPoint(
                            locationPoint.copy(tripId = currentTripId)
                        )
                    }
                    
                    // Update tracking metrics
                    val newMetrics = locationManager.updateMetrics(
                        _trackingMetrics.value,
                        locationPoint,
                        previousLocation
                    )
                    
                    // Update elapsed time
                    val elapsedTime = if (trackingStartTime > 0) {
                        System.currentTimeMillis() - trackingStartTime
                    } else {
                        0L
                    }
                    
                    _trackingMetrics.value = newMetrics.copy(elapsedTime = elapsedTime)
                    previousLocation = locationPoint
                }
            } catch (e: Exception) {
                println("Error in location updates: ${e.message}")
                e.printStackTrace()
            }
        }
    }
    
    /**
     * Load trip metrics from existing trip
     */
    private suspend fun loadTripMetrics(trip: Trip) {
        val locationPoints = tripRepository.getAllLocationPointsForTrip(trip.id)
        if (locationPoints.isNotEmpty()) {
            var totalDistance = 0.0
            var maxSpeed = 0f
            var totalSpeed = 0f
            
            locationPoints.forEachIndexed { index, point ->
                if (index > 0) {
                    val distance = locationManager.calculateDistance(
                        point.latitude, point.longitude,
                        locationPoints[index - 1].latitude, locationPoints[index - 1].longitude
                    )
                    totalDistance += distance
                }
                
                if (point.speed > maxSpeed) {
                    maxSpeed = point.speed
                }
                totalSpeed += point.speed
            }
            
            val averageSpeed = if (locationPoints.isNotEmpty()) {
                totalSpeed / locationPoints.size
            } else 0f
            
            _trackingMetrics.value = TrackingMetrics(
                currentSpeed = locationPoints.lastOrNull()?.speed ?: 0f,
                averageSpeed = averageSpeed,
                maxSpeed = maxSpeed,
                distance = totalDistance,
                elapsedTime = trip.duration,
                locationCount = locationPoints.size
            )
            
            _currentLocation.value = locationPoints.lastOrNull()
            previousLocation = if (locationPoints.size > 1) {
                locationPoints[locationPoints.size - 2]
            } else null
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        locationUpdateJob?.cancel()
        locationManager.stopLocationUpdates()
    }
}
