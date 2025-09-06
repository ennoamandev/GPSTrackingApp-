package com.example.gpstrackingapp.data.model

import org.junit.Test
import org.junit.Assert.*
import java.util.Date

/**
 * Unit tests for Trip data class
 */
class TripTest {

    @Test
    fun `getFormattedDuration should return correct format for hours minutes seconds`() {
        // Given
        val trip = Trip(
            id = 1,
            startTime = Date(),
            duration = 3661000L // 1 hour, 1 minute, 1 second
        )

        // When
        val result = trip.getFormattedDuration()

        // Then
        assertEquals("1h 1m 1s", result)
    }

    @Test
    fun `getFormattedDuration should return correct format for minutes seconds only`() {
        // Given
        val trip = Trip(
            id = 1,
            startTime = Date(),
            duration = 61000L // 1 minute, 1 second
        )

        // When
        val result = trip.getFormattedDuration()

        // Then
        assertEquals("1m 1s", result)
    }

    @Test
    fun `getFormattedDuration should return correct format for seconds only`() {
        // Given
        val trip = Trip(
            id = 1,
            startTime = Date(),
            duration = 5000L // 5 seconds
        )

        // When
        val result = trip.getFormattedDuration()

        // Then
        assertEquals("5s", result)
    }

    @Test
    fun `getFormattedDistance should return correct format for kilometers`() {
        // Given
        val trip = Trip(
            id = 1,
            startTime = Date(),
            totalDistance = 1500.0 // 1.5 km
        )

        // When
        val result = trip.getFormattedDistance()

        // Then
        assertEquals("1.50 km", result)
    }

    @Test
    fun `getFormattedDistance should return correct format for meters`() {
        // Given
        val trip = Trip(
            id = 1,
            startTime = Date(),
            totalDistance = 500.0 // 500 meters
        )

        // When
        val result = trip.getFormattedDistance()

        // Then
        assertEquals("500 m", result)
    }

    @Test
    fun `getFormattedSpeed should return correct format for kmh`() {
        // Given
        val trip = Trip(
            id = 1,
            startTime = Date(),
            averageSpeed = 10.0f // 10 m/s = 36 km/h
        )

        // When
        val result = trip.getFormattedSpeed()

        // Then
        assertEquals("36.0 km/h", result)
    }

    @Test
    fun `trip should be created with correct default values`() {
        // Given
        val startTime = Date()

        // When
        val trip = Trip(startTime = startTime)

        // Then
        assertEquals(0L, trip.id)
        assertEquals(startTime, trip.startTime)
        assertNull(trip.endTime)
        assertEquals(0L, trip.duration)
        assertEquals(0.0, trip.totalDistance, 0.001)
        assertEquals(0f, trip.averageSpeed, 0.001f)
        assertEquals(0f, trip.maxSpeed, 0.001f)
        assertFalse(trip.isCompleted)
        assertNotNull(trip.createdAt)
    }
}
