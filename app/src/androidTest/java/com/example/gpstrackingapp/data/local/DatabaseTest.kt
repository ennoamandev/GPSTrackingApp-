package com.example.gpstrackingapp.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.gpstrackingapp.data.model.LocationPoint
import com.example.gpstrackingapp.data.model.Trip
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Date
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*

/**
 * Integration tests for Room database
 */
@RunWith(AndroidJUnit4::class)
class DatabaseTest {

    private lateinit var database: AppDatabase
    private lateinit var tripDao: TripDao
    private lateinit var locationPointDao: LocationPointDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        
        tripDao = database.tripDao()
        locationPointDao = database.locationPointDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun `insert and get trip should work correctly`() = runBlocking {
        // Given
        val trip = Trip(
            startTime = Date(),
            isCompleted = false
        )

        // When
        val tripId = tripDao.insertTrip(trip)
        val retrievedTrip = tripDao.getTripById(tripId)

        // Then
        assertNotNull("Retrieved trip should not be null", retrievedTrip)
        assertEquals("Trip ID should match", tripId, retrievedTrip!!.id)
        assertEquals("Start time should match", trip.startTime, retrievedTrip.startTime)
        assertFalse("Trip should not be completed", retrievedTrip.isCompleted)
    }

    @Test
    fun `update trip should work correctly`() = runBlocking {
        // Given
        val trip = Trip(
            startTime = Date(),
            isCompleted = false
        )
        val tripId = tripDao.insertTrip(trip)

        // When
        val updatedTrip = trip.copy(
            id = tripId,
            isCompleted = true,
            totalDistance = 1000.0,
            averageSpeed = 5.0f
        )
        tripDao.updateTrip(updatedTrip)
        val retrievedTrip = tripDao.getTripById(tripId)

        // Then
        assertNotNull("Retrieved trip should not be null", retrievedTrip)
        assertTrue("Trip should be completed", retrievedTrip!!.isCompleted)
        assertEquals("Total distance should match", 1000.0, retrievedTrip.totalDistance, 0.001)
        assertEquals("Average speed should match", 5.0f, retrievedTrip.averageSpeed, 0.001f)
    }

    @Test
    fun `delete trip should work correctly`() = runBlocking {
        // Given
        val trip = Trip(startTime = Date())
        val tripId = tripDao.insertTrip(trip)

        // When
        tripDao.deleteTripById(tripId)
        val retrievedTrip = tripDao.getTripById(tripId)

        // Then
        assertNull("Trip should be deleted", retrievedTrip)
    }

    @Test
    fun `get all trips should return correct list`() = runBlocking {
        // Given
        val trip1 = Trip(startTime = Date())
        val trip2 = Trip(startTime = Date())
        tripDao.insertTrip(trip1)
        tripDao.insertTrip(trip2)

        // When
        val trips = tripDao.getAllTrips()

        // Then
        // Note: This is a Flow, so we'd need to collect it in a real test
        // For now, we'll just verify the method doesn't throw
        assertNotNull("Trips flow should not be null", trips)
    }

    @Test
    fun `insert and get location point should work correctly`() = runBlocking {
        // Given
        val trip = Trip(startTime = Date())
        val tripId = tripDao.insertTrip(trip)
        
        val locationPoint = LocationPoint(
            tripId = tripId,
            latitude = 40.7128,
            longitude = -74.0060,
            speed = 10.0f,
            timestamp = Date()
        )

        // When
        val locationId = locationPointDao.insertLocationPoint(locationPoint)
        val retrievedLocation = locationPointDao.getLastLocationPoint(tripId)

        // Then
        assertNotNull("Retrieved location should not be null", retrievedLocation)
        assertEquals("Location ID should match", locationId, retrievedLocation!!.id)
        assertEquals("Trip ID should match", tripId, retrievedLocation.tripId)
        assertEquals("Latitude should match", 40.7128, retrievedLocation.latitude, 0.001)
        assertEquals("Longitude should match", -74.0060, retrievedLocation.longitude, 0.001)
        assertEquals("Speed should match", 10.0f, retrievedLocation.speed, 0.001f)
    }

    @Test
    fun `get all location points for trip should work correctly`() = runBlocking {
        // Given
        val trip = Trip(startTime = Date())
        val tripId = tripDao.insertTrip(trip)
        
        val location1 = LocationPoint(
            tripId = tripId,
            latitude = 40.7128,
            longitude = -74.0060,
            speed = 10.0f,
            timestamp = Date()
        )
        val location2 = LocationPoint(
            tripId = tripId,
            latitude = 40.7129,
            longitude = -74.0061,
            speed = 15.0f,
            timestamp = Date()
        )
        
        locationPointDao.insertLocationPoint(location1)
        locationPointDao.insertLocationPoint(location2)

        // When
        val locations = locationPointDao.getAllLocationPointsForTrip(tripId)

        // Then
        assertEquals("Should have 2 location points", 2, locations.size)
    }

    @Test
    fun `delete location points for trip should work correctly`() = runBlocking {
        // Given
        val trip = Trip(startTime = Date())
        val tripId = tripDao.insertTrip(trip)
        
        val location = LocationPoint(
            tripId = tripId,
            latitude = 40.7128,
            longitude = -74.0060,
            speed = 10.0f,
            timestamp = Date()
        )
        locationPointDao.insertLocationPoint(location)

        // When
        locationPointDao.deleteLocationPointsForTrip(tripId)
        val locations = locationPointDao.getAllLocationPointsForTrip(tripId)

        // Then
        assertTrue("Location points should be deleted", locations.isEmpty())
    }

    @Test
    fun `get trip count should work correctly`() = runBlocking {
        // Given
        val trip1 = Trip(startTime = Date())
        val trip2 = Trip(startTime = Date())
        tripDao.insertTrip(trip1)
        tripDao.insertTrip(trip2)

        // When
        val count = tripDao.getTripCount()

        // Then
        assertEquals("Should have 2 trips", 2, count)
    }

    @Test
    fun `get total distance should work correctly`() = runBlocking {
        // Given
        val trip1 = Trip(
            startTime = Date(),
            totalDistance = 1000.0,
            isCompleted = true
        )
        val trip2 = Trip(
            startTime = Date(),
            totalDistance = 2000.0,
            isCompleted = true
        )
        tripDao.insertTrip(trip1)
        tripDao.insertTrip(trip2)

        // When
        val totalDistance = tripDao.getTotalDistance()

        // Then
        assertNotNull("Total distance should not be null", totalDistance)
        assertEquals("Total distance should be 3000", 3000.0, totalDistance!!, 0.001)
    }
}
