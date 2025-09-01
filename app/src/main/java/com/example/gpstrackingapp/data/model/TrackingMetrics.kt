package com.example.gpstrackingapp.data.model

/**
 * Represents real-time tracking metrics during GPS tracking
 */
data class TrackingMetrics(
    val currentSpeed: Float = 0f, // in m/s
    val averageSpeed: Float = 0f, // in m/s
    val maxSpeed: Float = 0f, // in m/s
    val distance: Double = 0.0, // in meters
    val elapsedTime: Long = 0L, // in milliseconds
    val locationCount: Int = 0
) {
    /**
     * Get formatted current speed string
     */
    fun getFormattedCurrentSpeed(): String {
        val kmh = currentSpeed * 3.6 // Convert m/s to km/h
        return "%.1f km/h".format(kmh)
    }
    
    /**
     * Get formatted average speed string
     */
    fun getFormattedAverageSpeed(): String {
        val kmh = averageSpeed * 3.6 // Convert m/s to km/h
        return "%.1f km/h".format(kmh)
    }
    
    /**
     * Get formatted max speed string
     */
    fun getFormattedMaxSpeed(): String {
        val kmh = maxSpeed * 3.6 // Convert m/s to km/h
        return "%.1f km/h".format(kmh)
    }
    
    /**
     * Get formatted distance string
     */
    fun getFormattedDistance(): String {
        return when {
            distance >= 1000 -> "%.2f km".format(distance / 1000)
            else -> "%.0f m".format(distance)
        }
    }
    
    /**
     * Get formatted elapsed time string
     */
    fun getFormattedElapsedTime(): String {
        val hours = elapsedTime / (1000 * 60 * 60)
        val minutes = (elapsedTime % (1000 * 60 * 60)) / (1000 * 60)
        val seconds = (elapsedTime % (1000 * 60)) / 1000
        
        return when {
            hours > 0 -> "${hours}h ${minutes}m ${seconds}s"
            minutes > 0 -> "${minutes}m ${seconds}s"
            else -> "${seconds}s"
        }
    }
}
