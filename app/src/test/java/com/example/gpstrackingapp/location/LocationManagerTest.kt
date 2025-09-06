package com.example.gpstrackingapp.location

import android.content.Context
import com.example.gpstrackingapp.data.model.LocationPoint
import com.example.gpstrackingapp.data.model.TrackingMetrics
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.util.Date

/**
 * Unit tests for LocationManager utility functions
 */
class LocationManagerTest {

    @Mock
    private lateinit var mockContext: Context

    private lateinit var locationManager: LocationManager

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        locationManager = LocationManager(mockContext)
    }

    @Test
    fun `calculateDistance should return correct distance between two points`() {
        // Given - New York to Los Angeles (approximately 3944 km)
        val nyLat = 40.7128
        val nyLon = -74.0060
        val laLat = 34.0522
        val laLon = -118.2437

        // When
        val distance = locationManager.calculateDistance(nyLat, nyLon, laLat, laLon)

        // Then - Allow for some margin of error
        assertTrue("Distance should be approximately 3944 km", distance > 3900000)
        assertTrue("Distance should be less than 4000 km", distance < 4000000)
    }

    @Test
    fun `calculateDistance should return zero for same coordinates`() {
        // Given
        val lat = 40.7128
        val lon = -74.0060

        // When
        val distance = locationManager.calculateDistance(lat, lon, lat, lon)

        // Then
        assertEquals(0.0, distance, 0.001)
    }

    @Test
    fun `calculateBearing should return correct bearing`() {
        // Given - North to East (should be 90 degrees)
        val lat1 = 0.0
        val lon1 = 0.0
        val lat2 = 0.0
        val lon2 = 1.0

        // When
        val bearing = locationManager.calculateBearing(lat1, lon1, lat2, lon2)

        // Then - Allow for some margin of error
        assertTrue("Bearing should be approximately 90 degrees", bearing > 85)
        assertTrue("Bearing should be approximately 90 degrees", bearing < 95)
    }

    @Test
    fun `isMoving should return true when distance exceeds threshold`() {
        // Given
        val currentLat = 40.7128
        val currentLon = -74.0060
        val previousLat = 40.7128
        val previousLon = -74.0059 // Small distance
        val threshold = 5.0 // 5 meters

        // When
        val isMoving = locationManager.isMoving(currentLat, currentLon, previousLat, previousLon, threshold)

        // Then
        assertTrue("Should be considered moving when distance exceeds threshold", isMoving)
    }

    @Test
    fun `isMoving should return false when distance is below threshold`() {
        // Given
        val currentLat = 40.7128
        val currentLon = -74.0060
        val previousLat = 40.7128
        val previousLon = -74.0060 // Same location
        val threshold = 10.0 // 10 meters

        // When
        val isMoving = locationManager.isMoving(currentLat, currentLon, previousLat, previousLon, threshold)

        // Then
        assertFalse("Should not be considered moving when distance is below threshold", isMoving)
    }

    @Test
    fun `updateMetrics should correctly update distance`() {
        // Given
        val initialMetrics = TrackingMetrics(distance = 100.0)
        val newLocation = LocationPoint(
            tripId = 1L,
            latitude = 40.7128,
            longitude = -74.0060,
            speed = 10.0f,
            timestamp = Date()
        )
        val previousLocation = LocationPoint(
            tripId = 1L,
            latitude = 40.7127,
            longitude = -74.0060,
            speed = 5.0f,
            timestamp = Date()
        )

        // When
        val updatedMetrics = locationManager.updateMetrics(initialMetrics, newLocation, previousLocation)

        // Then
        assertTrue("Distance should be increased", updatedMetrics.distance > initialMetrics.distance)
        assertEquals(10.0f, updatedMetrics.currentSpeed, 0.001f)
        assertEquals(1, updatedMetrics.locationCount)
    }

    @Test
    fun `updateMetrics should correctly update max speed`() {
        // Given
        val initialMetrics = TrackingMetrics(maxSpeed = 5.0f)
        val newLocation = LocationPoint(
            tripId = 1L,
            latitude = 40.7128,
            longitude = -74.0060,
            speed = 15.0f,
            timestamp = Date()
        )

        // When
        val updatedMetrics = locationManager.updateMetrics(initialMetrics, newLocation, null)

        // Then
        assertEquals(15.0f, updatedMetrics.maxSpeed, 0.001f)
        assertEquals(15.0f, updatedMetrics.currentSpeed, 0.001f)
        assertEquals(15.0f, updatedMetrics.averageSpeed, 0.001f)
    }

    @Test
    fun `updateMetrics should handle first location correctly`() {
        // Given
        val initialMetrics = TrackingMetrics()
        val newLocation = LocationPoint(
            tripId = 1L,
            latitude = 40.7128,
            longitude = -74.0060,
            speed = 10.0f,
            timestamp = Date()
        )

        // When
        val updatedMetrics = locationManager.updateMetrics(initialMetrics, newLocation, null)

        // Then
        assertEquals(10.0f, updatedMetrics.currentSpeed, 0.001f)
        assertEquals(10.0f, updatedMetrics.averageSpeed, 0.001f)
        assertEquals(10.0f, updatedMetrics.maxSpeed, 0.001f)
        assertEquals(0.0, updatedMetrics.distance, 0.001) // No previous location
        assertEquals(1, updatedMetrics.locationCount)
    }
}
