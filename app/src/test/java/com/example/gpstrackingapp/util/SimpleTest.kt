package com.example.gpstrackingapp.util

import org.junit.Test
import org.junit.Assert.*

/**
 * Simple utility tests that don't require complex mocking
 */
class SimpleTest {

    @Test
    fun `basic math operations should work correctly`() {
        // Given
        val a = 5
        val b = 3

        // When
        val sum = a + b
        val product = a * b

        // Then
        assertEquals(8, sum)
        assertEquals(15, product)
    }

    @Test
    fun `string operations should work correctly`() {
        // Given
        val baseString = "GPS Tracking"
        val expectedLength = 12

        // When
        val actualLength = baseString.length
        val upperCase = baseString.uppercase()

        // Then
        assertEquals(expectedLength, actualLength)
        assertEquals("GPS TRACKING", upperCase)
    }

    @Test
    fun `list operations should work correctly`() {
        // Given
        val numbers = listOf(1, 2, 3, 4, 5)

        // When
        val sum = numbers.sum()
        val average = numbers.average()
        val max = numbers.maxOrNull()

        // Then
        assertEquals(15, sum)
        assertEquals(3.0, average, 0.001)
        assertEquals(5, max)
    }

    @Test
    fun `date operations should work correctly`() {
        // Given
        val currentTime = System.currentTimeMillis()

        // When
        val date = java.util.Date(currentTime)

        // Then
        assertNotNull("Date should not be null", date)
        assertEquals("Time should match", currentTime, date.time)
    }

    @Test
    fun `coordinate validation should work correctly`() {
        // Given
        val validLatitude = 40.7128
        val validLongitude = -74.0060

        // When
        val isLatitudeValid = validLatitude in -90.0..90.0
        val isLongitudeValid = validLongitude in -180.0..180.0

        // Then
        assertTrue("Latitude should be valid", isLatitudeValid)
        assertTrue("Longitude should be valid", isLongitudeValid)
    }

    @Test
    fun `speed conversion should work correctly`() {
        // Given
        val speedInMps = 10.0f // 10 meters per second
        val expectedKmh = 36.0f // 36 km/h

        // When
        val actualKmh = speedInMps * 3.6f

        // Then
        assertEquals(expectedKmh, actualKmh, 0.001f)
    }

    @Test
    fun `distance calculation should work correctly`() {
        // Given
        val distanceInMeters = 1000.0
        val expectedKilometers = 1.0

        // When
        val actualKilometers = distanceInMeters / 1000.0

        // Then
        assertEquals(expectedKilometers, actualKilometers, 0.001)
    }
}
