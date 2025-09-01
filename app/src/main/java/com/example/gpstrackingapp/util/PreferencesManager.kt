package com.example.gpstrackingapp.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "gps_tracking_preferences")

/**
 * Manages app preferences using DataStore
 */
@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    companion object {
        private val LOCATION_UPDATE_INTERVAL = longPreferencesKey("location_update_interval")
        private val BACKGROUND_TRACKING = booleanPreferencesKey("background_tracking")
        private val AUTO_STOP_TRACKING = booleanPreferencesKey("auto_stop_tracking")
        private val AUTO_STOP_DELAY = intPreferencesKey("auto_stop_delay")
        private val UNITS_SYSTEM = stringPreferencesKey("units_system")
    }
    
    /**
     * Get location update interval in milliseconds
     */
    val locationUpdateInterval: Flow<Long> = context.dataStore.data
        .map { preferences ->
            preferences[LOCATION_UPDATE_INTERVAL] ?: 5000L // Default: 5 seconds
        }
    
    /**
     * Get background tracking setting
     */
    val backgroundTracking: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[BACKGROUND_TRACKING] ?: false
        }
    
    /**
     * Get auto-stop tracking setting
     */
    val autoStopTracking: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[AUTO_STOP_TRACKING] ?: true
        }
    
    /**
     * Get auto-stop delay in minutes
     */
    val autoStopDelay: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[AUTO_STOP_DELAY] ?: 5 // Default: 5 minutes
        }
    
    /**
     * Get units system preference
     */
    val unitsSystem: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[UNITS_SYSTEM] ?: "metric" // Default: metric
        }
    
    /**
     * Set location update interval
     */
    suspend fun setLocationUpdateInterval(intervalMs: Long) {
        context.dataStore.edit { preferences ->
            preferences[LOCATION_UPDATE_INTERVAL] = intervalMs
        }
    }
    
    /**
     * Set background tracking
     */
    suspend fun setBackgroundTracking(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[BACKGROUND_TRACKING] = enabled
        }
    }
    
    /**
     * Set auto-stop tracking
     */
    suspend fun setAutoStopTracking(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[AUTO_STOP_TRACKING] = enabled
        }
    }
    
    /**
     * Set auto-stop delay
     */
    suspend fun setAutoStopDelay(minutes: Int) {
        context.dataStore.edit { preferences ->
            preferences[AUTO_STOP_DELAY] = minutes
        }
    }
    
    /**
     * Set units system
     */
    suspend fun setUnitsSystem(system: String) {
        context.dataStore.edit { preferences ->
            preferences[UNITS_SYSTEM] = system
        }
    }
    
    /**
     * Get location update interval synchronously
     */
    suspend fun getLocationUpdateInterval(): Long {
        return locationUpdateInterval.map { it }.first()
    }
    
    /**
     * Get background tracking synchronously
     */
    suspend fun getBackgroundTracking(): Boolean {
        return backgroundTracking.map { it }.first()
    }
    
    /**
     * Get auto-stop tracking synchronously
     */
    suspend fun getAutoStopTracking(): Boolean {
        return autoStopTracking.map { it }.first()
    }
    
    /**
     * Get auto-stop delay synchronously
     */
    suspend fun getAutoStopDelay(): Int {
        return autoStopDelay.map { it }.first()
    }
    
    /**
     * Get units system synchronously
     */
    suspend fun getUnitsSystem(): String {
        return unitsSystem.map { it }.first()
    }
    
    /**
     * Reset all preferences to defaults
     */
    suspend fun resetToDefaults() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
