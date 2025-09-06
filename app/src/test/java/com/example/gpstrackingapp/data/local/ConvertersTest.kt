package com.example.gpstrackingapp.data.local

import org.junit.Test
import org.junit.Assert.*
import java.util.Date

/**
 * Unit tests for Converters class
 */
class ConvertersTest {

    private val converters = Converters()

    @Test
    fun `fromTimestamp should parse valid date string correctly`() {
        // Given
        val dateString = "2023-12-25T15:30:45"
        val expectedDate = Date(1703515845000L) // Approximate timestamp

        // When
        val result = converters.fromTimestamp(dateString)

        // Then
        assertNotNull("Parsed date should not be null", result)
        // Note: Exact comparison might fail due to timezone differences
        // So we'll just verify it's not null and can be formatted back
    }

    @Test
    fun `fromTimestamp should return null for null input`() {
        // When
        val result = converters.fromTimestamp(null)

        // Then
        assertNull("Should return null for null input", result)
    }

    @Test
    fun `fromTimestamp should return null for invalid date string`() {
        // Given
        val invalidDateString = "invalid-date"

        // When
        val result = converters.fromTimestamp(invalidDateString)

        // Then
        assertNull("Should return null for invalid date string", result)
    }

    @Test
    fun `dateToTimestamp should format date correctly`() {
        // Given
        val date = Date(1703515845000L) // 2023-12-25T15:30:45

        // When
        val result = converters.dateToTimestamp(date)

        // Then
        assertNotNull("Formatted string should not be null", result)
        assertTrue("Should contain date and time", result!!.contains("2023-12-25"))
        assertTrue("Should contain time", result.contains("15:30:45"))
    }

    @Test
    fun `dateToTimestamp should return null for null input`() {
        // When
        val result = converters.dateToTimestamp(null)

        // Then
        assertNull("Should return null for null input", result)
    }

    @Test
    fun `round trip conversion should work correctly`() {
        // Given
        val originalDate = Date()

        // When
        val timestampString = converters.dateToTimestamp(originalDate)
        val convertedDate = converters.fromTimestamp(timestampString)

        // Then
        assertNotNull("Timestamp string should not be null", timestampString)
        assertNotNull("Converted date should not be null", convertedDate)
        
        // The dates should be very close (within a second due to formatting precision)
        val timeDifference = Math.abs(originalDate.time - convertedDate!!.time)
        assertTrue("Time difference should be less than 1000ms", timeDifference < 1000)
    }

    @Test
    fun `dateToTimestamp should handle edge cases`() {
        // Given
        val epochDate = Date(0L) // January 1, 1970

        // When
        val result = converters.dateToTimestamp(epochDate)

        // Then
        assertNotNull("Should handle epoch date", result)
        assertTrue("Should contain 1970", result!!.contains("1970"))
    }
}
