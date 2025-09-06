package com.example.gpstrackingapp.data.model

import org.junit.Test
import org.junit.Assert.*
import java.util.Date

/**
 * Unit tests for LocationPoint data class
 */
class LocationPointTest {

    @Test
    fun `getFormattedSpeed should return correct format for kmh`() {
        // Given
        val locationPoint = LocationPoint(
            tripId = 1L,
            latitude = 40.7128,
            longitude = -74.0060,
            speed = 10.0f, // 10 m/s = 36 km/h
            timestamp = Date()
        )

        // When
        val result = locationPoint.getFormattedSpeed()

        // Then
        assertEquals("36.0 km/h", result)
    }

    @Test
    fun `getFormattedCoordinates should return correct format`() {
        // Given
        val locationPoint = LocationPoint(
            tripId = 1L,
            latitude = 40.7128,
            longitude = -74.0060,
            speed = 0f,
            timestamp = Date()
        )

        // When
        val result = locationPoint.getFormattedCoordinates()

        // Then
        assertEquals("40.712800, -74.006000", result)
    }

    @Test
    fun `locationPoint should be created with correct default values`() {
        // Given
        val tripId = 1L
        val latitude = 40.7128
        val longitude = -74.0060
        val speed = 5.0f
        val timestamp = Date()

        // When
        val locationPoint = LocationPoint(
            tripId = tripId,
            latitude = latitude,
            longitude = longitude,
            speed = speed,
            timestamp = timestamp
        )

        // Then
        assertEquals(0L, locationPoint.id)
        assertEquals(tripId, locationPoint.tripId)
        assertEquals(latitude, locationPoint.latitude, 0.001)
        assertEquals(longitude, locationPoint.longitude, 0.001)
        assertNull(locationPoint.altitude)
        assertEquals(speed, locationPoint.speed, 0.001f)
        assertNull(locationPoint.accuracy)
        assertNull(locationPoint.bearing)
        assertEquals(timestamp, locationPoint.timestamp)
        assertTrue(locationPoint.isMoving)
    }

    @Test
    fun `locationPoint should handle zero speed correctly`() {
        // Given
        val locationPoint = LocationPoint(
            tripId = 1L,
            latitude = 40.7128,
            longitude = -74.0060,
            speed = 0f,
            timestamp = Date()
        )

        // When
        val result = locationPoint.getFormattedSpeed()

        // Then
        assertEquals("0.0 km/h", result)
    }
}
