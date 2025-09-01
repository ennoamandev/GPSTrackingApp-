package com.example.gpstrackingapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.content.Context
import com.example.gpstrackingapp.data.model.Trip
import com.example.gpstrackingapp.data.model.LocationPoint
import com.example.gpstrackingapp.data.repository.TripRepository
import com.example.gpstrackingapp.util.DataExporter
import com.example.gpstrackingapp.util.ExportFormat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing trip history functionality
 */
@HiltViewModel
class TripHistoryViewModel @Inject constructor(
    private val tripRepository: TripRepository,
    private val context: Context
) : ViewModel() {
    
    private val _trips = MutableStateFlow<List<Trip>>(emptyList())
    val trips: StateFlow<List<Trip>> = _trips.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _isExporting = MutableStateFlow(false)
    val isExporting: StateFlow<Boolean> = _isExporting.asStateFlow()
    
    private val _exportProgress = MutableStateFlow<String?>(null)
    val exportProgress: StateFlow<String?> = _exportProgress.asStateFlow()
    
    init {
        loadTrips()
    }
    
    /**
     * Load all completed trips
     */
    private fun loadTrips() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                tripRepository.getCompletedTrips()
                    .catch { e ->
                        _error.value = e.message ?: "Failed to load trips"
                    }
                    .collect { tripList ->
                        _trips.value = tripList
                        _isLoading.value = false
                        _error.value = null
                    }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load trips"
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Delete a trip
     */
    fun deleteTrip(trip: Trip) {
        viewModelScope.launch {
            try {
                tripRepository.deleteTrip(trip)
                // Trips list will be automatically updated via Flow
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to delete trip"
            }
        }
    }
    
    /**
     * Delete trip by ID
     */
    fun deleteTripById(tripId: Long) {
        viewModelScope.launch {
            try {
                tripRepository.deleteTripById(tripId)
                // Trips list will be automatically updated via Flow
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to delete trip"
            }
        }
    }
    
    /**
     * Get trip statistics
     */
    fun getTripStatistics(): TripStatistics {
        val tripList = _trips.value
        if (tripList.isEmpty()) {
            return TripStatistics()
        }
        
        val totalTrips = tripList.size
        val totalDistance = tripList.sumOf { it.totalDistance }
        val totalDuration = tripList.sumOf { it.duration }
        val averageSpeed = tripList.map { it.averageSpeed }.average().toFloat()
        val maxSpeed = tripList.maxOfOrNull { it.maxSpeed } ?: 0f
        
        return TripStatistics(
            totalTrips = totalTrips,
            totalDistance = totalDistance,
            totalDuration = totalDuration,
            averageSpeed = averageSpeed,
            maxSpeed = maxSpeed
        )
    }
    
    /**
     * Refresh trips list
     */
    fun refreshTrips() {
        loadTrips()
    }
    
    /**
     * Clear error message
     */
    fun clearError() {
        _error.value = null
    }
    
    /**
     * Export single trip data
     */
    fun exportTrip(
        trip: Trip,
        locationPoints: List<LocationPoint>,
        format: ExportFormat,
        outputStream: java.io.OutputStream
    ): Result<Unit> {
        return try {
            _isExporting.value = true
            _exportProgress.value = "Exporting trip data..."
            
            val dataExporter = DataExporter(context)
            val result = when (format) {
                ExportFormat.CSV -> dataExporter.exportToCSV(trip, locationPoints, outputStream)
                ExportFormat.JSON -> dataExporter.exportToJSON(trip, locationPoints, outputStream)
            }
            
            _isExporting.value = false
            _exportProgress.value = null
            result
        } catch (e: Exception) {
            _isExporting.value = false
            _exportProgress.value = null
            _error.value = "Export failed: ${e.message}"
            Result.failure(e)
        }
    }
    
    /**
     * Export all trips data
     */
    fun exportAllTrips(
        format: ExportFormat,
        outputStream: java.io.OutputStream
    ): Result<Unit> {
        return try {
            _isExporting.value = true
            _exportProgress.value = "Exporting all trips data..."
            
            val trips = _trips.value
            val locationPointsMap = mutableMapOf<Long, List<LocationPoint>>()
            
            // Collect location points for all trips
            trips.forEach { trip ->
                val locationPoints = tripRepository.getAllLocationPointsForTrip(trip.id)
                locationPointsMap[trip.id] = locationPoints
            }
            
            val dataExporter = DataExporter(context)
            val result = when (format) {
                ExportFormat.CSV -> dataExporter.exportTripsToCSV(trips, locationPointsMap, outputStream)
                ExportFormat.JSON -> {
                    // For JSON, we'll export each trip individually and combine them
                    val combinedData = buildString {
                        appendLine("[")
                        trips.forEachIndexed { index, trip ->
                            val locationPoints = locationPointsMap[trip.id] ?: emptyList()
                            val tripJson = dataExporter.exportToJSON(trip, locationPoints, outputStream)
                            if (index < trips.size - 1) appendLine(",")
                        }
                        appendLine("]")
                    }
                    Result.success(Unit)
                }
            }
            
            _isExporting.value = false
            _exportProgress.value = null
            result
        } catch (e: Exception) {
            _isExporting.value = false
            _exportProgress.value = null
            _error.value = "Export failed: ${e.message}"
            Result.failure(e)
        }
    }
    
    /**
     * Get location points for a specific trip
     */
    suspend fun getLocationPointsForTrip(tripId: Long): List<LocationPoint> {
        return tripRepository.getAllLocationPointsForTrip(tripId)
    }
}

/**
 * Data class for trip statistics
 */
data class TripStatistics(
    val totalTrips: Int = 0,
    val totalDistance: Double = 0.0,
    val totalDuration: Long = 0L,
    val averageSpeed: Float = 0f,
    val maxSpeed: Float = 0f
) {
    fun getFormattedTotalDistance(): String {
        return when {
            totalDistance >= 1000 -> "%.2f km".format(totalDistance / 1000)
            else -> "%.0f m".format(totalDistance)
        }
    }
    
    fun getFormattedTotalDuration(): String {
        val hours = totalDuration / (1000 * 60 * 60)
        val minutes = (totalDuration % (1000 * 60 * 60)) / (1000 * 60)
        
        return when {
            hours > 0 -> "${hours}h ${minutes}m"
            else -> "${minutes}m"
        }
    }
    
    fun getFormattedAverageSpeed(): String {
        val kmh = averageSpeed * 3.6 // Convert m/s to km/h
        return "%.1f km/h".format(kmh)
    }
    
    fun getFormattedMaxSpeed(): String {
        val kmh = maxSpeed * 3.6 // Convert m/s to km/h
        return "%.1f km/h".format(kmh)
    }
}
