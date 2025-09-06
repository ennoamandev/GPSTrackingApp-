package com.example.gpstrackingapp.data.model

import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for TrackingMetrics data class
 */
class TrackingMetricsTest {

    @Test
    fun `trackingMetrics should be created with correct default values`() {
        // When
        val metrics = TrackingMetrics()

        // Then
        assertEquals(0f, metrics.currentSpeed, 0.001f)
        assertEquals(0f, metrics.averageSpeed, 0.001f)
        assertEquals(0f, metrics.maxSpeed, 0.001f)
        assertEquals(0.0, metrics.distance, 0.001)
        assertEquals(0L, metrics.elapsedTime)
        assertEquals(0, metrics.locationCount)
    }

    @Test
    fun `trackingMetrics copy should work correctly`() {
        // Given
        val originalMetrics = TrackingMetrics()
        val newSpeed = 15.0f
        val newDistance = 100.0

        // When
        val updatedMetrics = originalMetrics.copy(
            currentSpeed = newSpeed,
            distance = newDistance
        )

        // Then
        assertEquals(newSpeed, updatedMetrics.currentSpeed, 0.001f)
        assertEquals(newDistance, updatedMetrics.distance, 0.001)
        assertEquals(0f, updatedMetrics.averageSpeed, 0.001f) // Should remain unchanged
        assertEquals(0f, updatedMetrics.maxSpeed, 0.001f) // Should remain unchanged
        assertEquals(0L, updatedMetrics.elapsedTime) // Should remain unchanged
        assertEquals(0, updatedMetrics.locationCount) // Should remain unchanged
    }

    @Test
    fun `trackingMetrics should handle edge cases`() {
        // Given
        val metrics = TrackingMetrics(
            currentSpeed = Float.MAX_VALUE,
            averageSpeed = Float.MIN_VALUE,
            maxSpeed = 0f,
            distance = Double.MAX_VALUE,
            elapsedTime = Long.MAX_VALUE,
            locationCount = Int.MAX_VALUE
        )

        // Then
        assertEquals(Float.MAX_VALUE, metrics.currentSpeed, 0.001f)
        assertEquals(Float.MIN_VALUE, metrics.averageSpeed, 0.001f)
        assertEquals(0f, metrics.maxSpeed, 0.001f)
        assertEquals(Double.MAX_VALUE, metrics.distance, 0.001)
        assertEquals(Long.MAX_VALUE, metrics.elapsedTime)
        assertEquals(Int.MAX_VALUE, metrics.locationCount)
    }
}
