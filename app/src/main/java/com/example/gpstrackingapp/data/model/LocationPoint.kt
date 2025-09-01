package com.example.gpstrackingapp.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * Represents a GPS location point during tracking
 */
@Entity(
    tableName = "location_points",
    foreignKeys = [
        ForeignKey(
            entity = Trip::class,
            parentColumns = ["id"],
            childColumns = ["tripId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class LocationPoint(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tripId: Long,
    val latitude: Double,
    val longitude: Double,
    val altitude: Double? = null,
    val speed: Float, // in m/s
    val accuracy: Float? = null,
    val bearing: Float? = null,
    val timestamp: LocalDateTime,
    val isMoving: Boolean = true
) {
    /**
     * Get formatted speed string
     */
    fun getFormattedSpeed(): String {
        val kmh = speed * 3.6 // Convert m/s to km/h
        return "%.1f km/h".format(kmh)
    }
    
    /**
     * Get formatted coordinates string
     */
    fun getFormattedCoordinates(): String {
        return "%.6f, %.6f".format(latitude, longitude)
    }
}
