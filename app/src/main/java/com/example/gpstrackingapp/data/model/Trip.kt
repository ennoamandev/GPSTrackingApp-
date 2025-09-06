package com.example.gpstrackingapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Represents a GPS tracking trip
 */
@Entity(tableName = "trips")
data class Trip(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startTime: Date,
    val endTime: Date? = null,
    val duration: Long = 0, // in milliseconds
    val totalDistance: Double = 0.0, // in meters
    val averageSpeed: Float = 0f, // in m/s
    val maxSpeed: Float = 0f, // in m/s
    val isCompleted: Boolean = false,
    val createdAt: Date = Date()
) {
    /**
     * Get formatted duration string
     */
    fun getFormattedDuration(): String {
        val hours = duration / (1000 * 60 * 60)
        val minutes = (duration % (1000 * 60 * 60)) / (1000 * 60)
        val seconds = (duration % (1000 * 60)) / 1000
        
        return when {
            hours > 0 -> "${hours}h ${minutes}m ${seconds}s"
            minutes > 0 -> "${minutes}m ${seconds}s"
            else -> "${seconds}s"
        }
    }
    
    /**
     * Get formatted distance string
     */
    fun getFormattedDistance(): String {
        return when {
            totalDistance >= 1000 -> "%.2f km".format(totalDistance / 1000)
            else -> "%.0f m".format(totalDistance)
        }
    }
    
    /**
     * Get formatted speed string
     */
    fun getFormattedSpeed(): String {
        val kmh = averageSpeed * 3.6 // Convert m/s to km/h
        return "%.1f km/h".format(kmh)
    }
}
