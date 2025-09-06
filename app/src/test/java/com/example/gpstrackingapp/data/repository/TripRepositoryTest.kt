package com.example.gpstrackingapp.data.repository

import com.example.gpstrackingapp.data.local.TripDao
import com.example.gpstrackingapp.data.local.LocationPointDao
import com.example.gpstrackingapp.data.model.Trip
import com.example.gpstrackingapp.data.model.LocationPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.*
import java.util.Date

/**
 * Unit tests for TripRepository
 */
class TripRepositoryTest {

    @Mock
    private lateinit var tripDao: TripDao

    @Mock
    private lateinit var locationPointDao: LocationPointDao

    private lateinit var tripRepository: TripRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        tripRepository = TripRepository(tripDao, locationPointDao)
    }

    @Test
    fun `getAllTrips should return flow from tripDao`() {
        // Given
        val expectedTrips = listOf(
            Trip(id = 1, startTime = Date()),
            Trip(id = 2, startTime = Date())
        )
        whenever(tripDao.getAllTrips()).thenReturn(flowOf(expectedTrips))

        // When
        val result = tripRepository.getAllTrips()

        // Then
        assertNotNull("Result should not be null", result)
        verify(tripDao).getAllTrips()
    }

    @Test
    fun `getCompletedTrips should return flow from tripDao`() {
        // Given
        val expectedTrips = listOf(
            Trip(id = 1, startTime = Date(), isCompleted = true),
            Trip(id = 2, startTime = Date(), isCompleted = true)
        )
        whenever(tripDao.getCompletedTrips()).thenReturn(flowOf(expectedTrips))

        // When
        val result = tripRepository.getCompletedTrips()

        // Then
        assertNotNull("Result should not be null", result)
        verify(tripDao).getCompletedTrips()
    }

    @Test
    fun `getTripById should return trip from tripDao`() = runBlocking {
        // Given
        val tripId = 1L
        val expectedTrip = Trip(id = tripId, startTime = Date())
        whenever(tripDao.getTripById(tripId)).thenReturn(expectedTrip)

        // When
        val result = tripRepository.getTripById(tripId)

        // Then
        assertEquals("Should return expected trip", expectedTrip, result)
        verify(tripDao).getTripById(tripId)
    }

    @Test
    fun `getCurrentTrip should return trip from tripDao`() = runBlocking {
        // Given
        val expectedTrip = Trip(id = 1L, startTime = Date(), isCompleted = false)
        whenever(tripDao.getCurrentTrip()).thenReturn(expectedTrip)

        // When
        val result = tripRepository.getCurrentTrip()

        // Then
        assertEquals("Should return expected trip", expectedTrip, result)
        verify(tripDao).getCurrentTrip()
    }

    @Test
    fun `insertTrip should return trip ID from tripDao`() = runBlocking {
        // Given
        val trip = Trip(startTime = Date())
        val expectedId = 1L
        whenever(tripDao.insertTrip(trip)).thenReturn(expectedId)

        // When
        val result = tripRepository.insertTrip(trip)

        // Then
        assertEquals("Should return expected ID", expectedId, result)
        verify(tripDao).insertTrip(trip)
    }

    @Test
    fun `updateTrip should call tripDao`() = runBlocking {
        // Given
        val trip = Trip(id = 1L, startTime = Date())

        // When
        tripRepository.updateTrip(trip)

        // Then
        verify(tripDao).updateTrip(trip)
    }

    @Test
    fun `deleteTrip should call tripDao`() = runBlocking {
        // Given
        val trip = Trip(id = 1L, startTime = Date())

        // When
        tripRepository.deleteTrip(trip)

        // Then
        verify(tripDao).deleteTrip(trip)
    }

    @Test
    fun `deleteTripById should call tripDao`() = runBlocking {
        // Given
        val tripId = 1L

        // When
        tripRepository.deleteTripById(tripId)

        // Then
        verify(tripDao).deleteTripById(tripId)
    }

    @Test
    fun `getTripCount should return count from tripDao`() = runBlocking {
        // Given
        val expectedCount = 5
        whenever(tripDao.getTripCount()).thenReturn(expectedCount)

        // When
        val result = tripRepository.getTripCount()

        // Then
        assertEquals("Should return expected count", expectedCount, result)
        verify(tripDao).getTripCount()
    }

    @Test
    fun `getTotalDistance should return distance from tripDao`() = runBlocking {
        // Given
        val expectedDistance = 1000.0
        whenever(tripDao.getTotalDistance()).thenReturn(expectedDistance)

        // When
        val result = tripRepository.getTotalDistance()

        // Then
        assertEquals("Should return expected distance", expectedDistance, result)
        verify(tripDao).getTotalDistance()
    }

    @Test
    fun `getAverageSpeed should return speed from tripDao`() = runBlocking {
        // Given
        val expectedSpeed = 15.5
        whenever(tripDao.getAverageSpeed()).thenReturn(expectedSpeed)

        // When
        val result = tripRepository.getAverageSpeed()

        // Then
        assertEquals("Should return expected speed", expectedSpeed, result)
        verify(tripDao).getAverageSpeed()
    }

    @Test
    fun `getLocationPointsForTrip should return flow from locationPointDao`() {
        // Given
        val tripId = 1L
        val expectedLocations = listOf(
            LocationPoint(tripId = tripId, latitude = 40.7128, longitude = -74.0060, speed = 10.0f, timestamp = Date()),
            LocationPoint(tripId = tripId, latitude = 40.7129, longitude = -74.0061, speed = 15.0f, timestamp = Date())
        )
        whenever(locationPointDao.getLocationPointsForTrip(tripId)).thenReturn(flowOf(expectedLocations))

        // When
        val result = tripRepository.getLocationPointsForTrip(tripId)

        // Then
        assertNotNull("Result should not be null", result)
        verify(locationPointDao).getLocationPointsForTrip(tripId)
    }

    @Test
    fun `getLastLocationPoint should return location from locationPointDao`() = runBlocking {
        // Given
        val tripId = 1L
        val expectedLocation = LocationPoint(tripId = tripId, latitude = 40.7128, longitude = -74.0060, speed = 10.0f, timestamp = Date())
        whenever(locationPointDao.getLastLocationPoint(tripId)).thenReturn(expectedLocation)

        // When
        val result = tripRepository.getLastLocationPoint(tripId)

        // Then
        assertEquals("Should return expected location", expectedLocation, result)
        verify(locationPointDao).getLastLocationPoint(tripId)
    }

    @Test
    fun `getAllLocationPointsForTrip should return list from locationPointDao`() = runBlocking {
        // Given
        val tripId = 1L
        val expectedLocations = listOf(
            LocationPoint(tripId = tripId, latitude = 40.7128, longitude = -74.0060, speed = 10.0f, timestamp = Date())
        )
        whenever(locationPointDao.getAllLocationPointsForTrip(tripId)).thenReturn(expectedLocations)

        // When
        val result = tripRepository.getAllLocationPointsForTrip(tripId)

        // Then
        assertEquals("Should return expected locations", expectedLocations, result)
        verify(locationPointDao).getAllLocationPointsForTrip(tripId)
    }

    @Test
    fun `insertLocationPoint should return ID from locationPointDao`() = runBlocking {
        // Given
        val locationPoint = LocationPoint(tripId = 1L, latitude = 40.7128, longitude = -74.0060, speed = 10.0f, timestamp = Date())
        val expectedId = 1L
        whenever(locationPointDao.insertLocationPoint(locationPoint)).thenReturn(expectedId)

        // When
        val result = tripRepository.insertLocationPoint(locationPoint)

        // Then
        assertEquals("Should return expected ID", expectedId, result)
        verify(locationPointDao).insertLocationPoint(locationPoint)
    }
}
