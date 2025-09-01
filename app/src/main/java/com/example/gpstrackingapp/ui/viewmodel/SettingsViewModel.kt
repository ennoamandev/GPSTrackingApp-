package com.example.gpstrackingapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gpstrackingapp.util.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing app settings
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {
    
    private val _locationUpdateInterval = MutableStateFlow(5000L) // Default: 5 seconds
    val locationUpdateInterval: StateFlow<Long> = _locationUpdateInterval.asStateFlow()
    
    private val _backgroundTracking = MutableStateFlow(false)
    val backgroundTracking: StateFlow<Boolean> = _backgroundTracking.asStateFlow()
    
    private val _autoStopTracking = MutableStateFlow(true)
    val autoStopTracking: StateFlow<Boolean> = _autoStopTracking.asStateFlow()
    
    private val _autoStopDelay = MutableStateFlow(5) // Default: 5 minutes
    val autoStopDelay: StateFlow<Int> = _autoStopDelay.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    init {
        loadSettings()
    }
    
    /**
     * Load current settings from preferences
     */
    private fun loadSettings() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _locationUpdateInterval.value = preferencesManager.getLocationUpdateInterval()
                _backgroundTracking.value = preferencesManager.getBackgroundTracking()
                _autoStopTracking.value = preferencesManager.getAutoStopTracking()
                _autoStopDelay.value = preferencesManager.getAutoStopDelay()
            } catch (e: Exception) {
                // Handle error loading settings
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Update location update interval
     */
    fun updateLocationUpdateInterval(intervalMs: Long) {
        viewModelScope.launch {
            try {
                preferencesManager.setLocationUpdateInterval(intervalMs)
                _locationUpdateInterval.value = intervalMs
            } catch (e: Exception) {
                // Handle error saving setting
            }
        }
    }
    
    /**
     * Update background tracking setting
     */
    fun updateBackgroundTracking(enabled: Boolean) {
        viewModelScope.launch {
            try {
                preferencesManager.setBackgroundTracking(enabled)
                _backgroundTracking.value = enabled
            } catch (e: Exception) {
                // Handle error saving setting
            }
        }
    }
    
    /**
     * Update auto-stop tracking setting
     */
    fun updateAutoStopTracking(enabled: Boolean) {
        viewModelScope.launch {
            try {
                preferencesManager.setAutoStopTracking(enabled)
                _autoStopTracking.value = enabled
            } catch (e: Exception) {
                // Handle error saving setting
            }
        }
    }
    
    /**
     * Update auto-stop delay
     */
    fun updateAutoStopDelay(minutes: Int) {
        viewModelScope.launch {
            try {
                preferencesManager.setAutoStopDelay(minutes)
                _autoStopDelay.value = minutes
            } catch (e: Exception) {
                // Handle error saving setting
            }
        }
    }
    
    /**
     * Get available location update intervals
     */
    fun getAvailableIntervals(): List<LocationUpdateInterval> {
        return listOf(
            LocationUpdateInterval(1000L, "1 second"),
            LocationUpdateInterval(5000L, "5 seconds"),
            LocationUpdateInterval(10000L, "10 seconds")
        )
    }
    
    /**
     * Get available auto-stop delays
     */
    fun getAvailableAutoStopDelays(): List<AutoStopDelay> {
        return listOf(
            AutoStopDelay(1, "1 minute"),
            AutoStopDelay(3, "3 minutes"),
            AutoStopDelay(5, "5 minutes"),
            AutoStopDelay(10, "10 minutes"),
            AutoStopDelay(15, "15 minutes")
        )
    }
    
    /**
     * Reset settings to defaults
     */
    fun resetToDefaults() {
        viewModelScope.launch {
            try {
                preferencesManager.resetToDefaults()
                loadSettings()
            } catch (e: Exception) {
                // Handle error resetting settings
            }
        }
    }
}

/**
 * Data class for location update interval options
 */
data class LocationUpdateInterval(
    val intervalMs: Long,
    val displayName: String
)

/**
 * Data class for auto-stop delay options
 */
data class AutoStopDelay(
    val minutes: Int,
    val displayName: String
)
