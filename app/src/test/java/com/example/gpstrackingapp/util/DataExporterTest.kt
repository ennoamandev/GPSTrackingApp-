package com.example.gpstrackingapp.util

import android.content.Context
import com.example.gpstrackingapp.data.model.Trip
import com.example.gpstrackingapp.data.model.LocationPoint
import com.example.gpstrackingapp.util.ExportFormat
import org.junit.Test
import org.junit.Assert.*
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.io.ByteArrayOutputStream
import java.util.Date

/**
 * Unit tests for DataExporter
 */
class DataExporterTest {

    @Mock
    private lateinit var mockContext: Context

    @Test
    fun `exportToCSV should generate valid CSV format`() {
        // Given
        val trip = Trip(
            id = 1L,
            startTime = Date(1703515845000L),
            endTime = Date(1703519445000L),
            duration = 3600000L,
            totalDistance = 1000.0,
            averageSpeed = 10.0f,
            maxSpeed = 15.0f,
            isCompleted = true
        )
        
        val locationPoints = listOf(
            LocationPoint(
                tripId = 1L,
                latitude = 40.7128,
                longitude = -74.0060,
                speed = 10.0f,
                timestamp = Date(1703515845000L)
            ),
            LocationPoint(
                tripId = 1L,
                latitude = 40.7129,
                longitude = -74.0061,
                speed = 15.0f,
                timestamp = Date(1703515905000L)
            )
        )

        MockitoAnnotations.openMocks(this)
        val dataExporter = DataExporter(mockContext)
        val outputStream = ByteArrayOutputStream()

        // When
        val result = dataExporter.exportToCSV(trip, locationPoints, outputStream)

        // Then
        assertTrue("Export should be successful", result.isSuccess)
        val csvContent = outputStream.toString()
        assertTrue("Should contain trip data", csvContent.contains("Trip Information"))
        assertTrue("Should contain location data", csvContent.contains("Latitude"))
        assertTrue("Should contain speed data", csvContent.contains("Speed"))
    }

    @Test
    fun `exportToJSON should generate valid JSON format`() {
        // Given
        val trip = Trip(
            id = 1L,
            startTime = Date(1703515845000L),
            endTime = Date(1703519445000L),
            duration = 3600000L,
            totalDistance = 1000.0,
            averageSpeed = 10.0f,
            maxSpeed = 15.0f,
            isCompleted = true
        )
        
        val locationPoints = listOf(
            LocationPoint(
                tripId = 1L,
                latitude = 40.7128,
                longitude = -74.0060,
                speed = 10.0f,
                timestamp = Date(1703515845000L)
            )
        )

        MockitoAnnotations.openMocks(this)
        val dataExporter = DataExporter(mockContext)
        val outputStream = ByteArrayOutputStream()

        // When
        val result = dataExporter.exportToJSON(trip, locationPoints, outputStream)

        // Then
        assertTrue("Export should be successful", result.isSuccess)
        val jsonContent = outputStream.toString()
        assertTrue("Should contain trip object", jsonContent.contains("\"id\""))
        assertTrue("Should contain location points array", jsonContent.contains("\"locationPoints\""))
        assertTrue("Should be valid JSON", jsonContent.startsWith("{") && jsonContent.endsWith("}"))
    }

    @Test
    fun `exportToCSV should handle empty location points list`() {
        // Given
        val trip = Trip(
            id = 1L,
            startTime = Date(),
            isCompleted = true
        )
        val locationPoints = emptyList<LocationPoint>()

        MockitoAnnotations.openMocks(this)
        val dataExporter = DataExporter(mockContext)
        val outputStream = ByteArrayOutputStream()

        // When
        val result = dataExporter.exportToCSV(trip, locationPoints, outputStream)

        // Then
        assertTrue("Export should be successful even with empty list", result.isSuccess)
        val content = outputStream.toString()
        assertTrue("Should contain trip information", content.contains("Trip Information"))
    }

    @Test
    fun `exportToCSV should handle null endTime gracefully`() {
        // Given
        val trip = Trip(
            id = 1L,
            startTime = Date(),
            endTime = null,
            isCompleted = false
        )
        val locationPoints = emptyList<LocationPoint>()

        MockitoAnnotations.openMocks(this)
        val dataExporter = DataExporter(mockContext)
        val outputStream = ByteArrayOutputStream()

        // When
        val result = dataExporter.exportToCSV(trip, locationPoints, outputStream)

        // Then
        assertTrue("Export should be successful even with null endTime", result.isSuccess)
        val content = outputStream.toString()
        assertTrue("Should contain trip data", content.contains("Trip Information"))
    }
}
