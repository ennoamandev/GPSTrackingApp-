package com.example.gpstrackingapp.util

import android.content.Context
import android.net.Uri
import com.example.gpstrackingapp.data.model.LocationPoint
import com.example.gpstrackingapp.data.model.Trip
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.*

/**
 * Utility class for exporting trip data to various formats
 */
class DataExporter(private val context: Context) {
    
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    
    /**
     * Export trip data to CSV format
     */
    fun exportToCSV(
        trip: Trip,
        locationPoints: List<LocationPoint>,
        outputStream: java.io.OutputStream
    ): Result<Unit> {
        return try {
            val writer = OutputStreamWriter(outputStream)
            
            // Write trip header
            writer.write("Trip Information\n")
            writer.write("Trip ID,${trip.id}\n")
            writer.write("Start Time,${dateFormat.format(trip.startTime)}\n")
            writer.write("End Time,${if (trip.endTime != null) dateFormat.format(trip.endTime) else "N/A"}\n")
            writer.write("Duration (ms),${trip.duration}\n")
            writer.write("Total Distance (m),${String.format("%.2f", trip.totalDistance)}\n")
            writer.write("Average Speed (m/s),${String.format("%.2f", trip.averageSpeed)}\n")
            writer.write("Max Speed (m/s),${String.format("%.2f", trip.maxSpeed)}\n")
            writer.write("Completed,${trip.isCompleted}\n")
            writer.write("\n")
            
            // Write location points header
            writer.write("Location Points\n")
            writer.write("Timestamp,Latitude,Longitude,Altitude,Speed,Accuracy,Bearing,IsMoving\n")
            
            // Write location points data
            locationPoints.forEach { point ->
                writer.write("${dateFormat.format(point.timestamp),")
                writer.write("${String.format("%.6f", point.latitude)},")
                writer.write("${String.format("%.6f", point.longitude)},")
                writer.write("${point.altitude?.let { String.format("%.2f", it) } ?: "N/A"},")
                writer.write("${String.format("%.2f", point.speed)},")
                writer.write("${point.accuracy?.let { String.format("%.2f", it) } ?: "N/A"},")
                writer.write("${point.bearing?.let { String.format("%.2f", it) } ?: "N/A"},")
                writer.write("${point.isMoving}\n")
            }
            
            writer.flush()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Export trip data to JSON format
     */
    fun exportToJSON(
        trip: Trip,
        locationPoints: List<LocationPoint>,
        outputStream: java.io.OutputStream
    ): Result<Unit> {
        return try {
            val writer = OutputStreamWriter(outputStream)
            
            val jsonData = buildString {
                appendLine("{")
                appendLine("  \"trip\": {")
                appendLine("    \"id\": ${trip.id},")
                appendLine("    \"startTime\": \"${dateFormat.format(trip.startTime)}\",")
                appendLine("    \"endTime\": ${if (trip.endTime != null) "\"${dateFormat.format(trip.endTime)}\"" else "null},")
                appendLine("    \"duration\": ${trip.duration},")
                appendLine("    \"totalDistance\": ${String.format("%.2f", trip.totalDistance)},")
                appendLine("    \"averageSpeed\": ${String.format("%.2f", trip.averageSpeed)},")
                appendLine("    \"maxSpeed\": ${String.format("%.2f", trip.maxSpeed)},")
                appendLine("    \"isCompleted\": ${trip.isCompleted},")
                appendLine("    \"createdAt\": \"${dateFormat.format(trip.createdAt)}\"")
                appendLine("  },")
                appendLine("  \"locationPoints\": [")
                
                locationPoints.forEachIndexed { index, point ->
                    appendLine("    {")
                    appendLine("      \"timestamp\": \"${dateFormat.format(point.timestamp)}\",")
                    appendLine("      \"latitude\": ${String.format("%.6f", point.latitude)},")
                    appendLine("      \"longitude\": ${String.format("%.6f", point.longitude)},")
                    appendLine("      \"altitude\": ${point.altitude?.let { String.format("%.2f", it) } ?: "null"},")
                    appendLine("      \"speed\": ${String.format("%.2f", point.speed)},")
                    appendLine("      \"accuracy\": ${point.accuracy?.let { String.format("%.2f", it) } ?: "null"},")
                    appendLine("      \"bearing\": ${point.bearing?.let { String.format("%.2f", it) } ?: "null"},")
                    appendLine("      \"isMoving\": ${point.isMoving}")
                    appendLine("    }${if (index < locationPoints.size - 1) "," else ""}")
                }
                
                appendLine("  ]")
                appendLine("}")
            }
            
            writer.write(jsonData)
            writer.flush()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Export multiple trips to CSV format
     */
    fun exportTripsToCSV(
        trips: List<Trip>,
        locationPointsMap: Map<Long, List<LocationPoint>>,
        outputStream: java.io.OutputStream
    ): Result<Unit> {
        return try {
            val writer = OutputStreamWriter(outputStream)
            
            // Write summary header
            writer.write("GPS Tracking App - Trips Summary\n")
            writer.write("Export Date,${dateFormat.format(Date())}\n")
            writer.write("Total Trips,${trips.size}\n")
            writer.write("\n")
            
            // Write trips summary
            writer.write("Trips Summary\n")
            writer.write("ID,Start Time,End Time,Duration (ms),Distance (m),Avg Speed (m/s),Max Speed (m/s),Completed\n")
            
            trips.forEach { trip ->
                writer.write("${trip.id},")
                writer.write("${dateFormat.format(trip.startTime)},")
                writer.write("${if (trip.endTime != null) dateFormat.format(trip.endTime) else "N/A"},")
                writer.write("${trip.duration},")
                writer.write("${String.format("%.2f", trip.totalDistance)},")
                writer.write("${String.format("%.2f", trip.averageSpeed)},")
                writer.write("${String.format("%.2f", trip.maxSpeed)},")
                writer.write("${trip.isCompleted}\n")
            }
            
            writer.write("\n")
            
            // Write detailed location data for each trip
            trips.forEach { trip ->
                val locationPoints = locationPointsMap[trip.id] ?: emptyList()
                if (locationPoints.isNotEmpty()) {
                    writer.write("Trip ${trip.id} - Location Points\n")
                    writer.write("Timestamp,Latitude,Longitude,Altitude,Speed,Accuracy,Bearing,IsMoving\n")
                    
                    locationPoints.forEach { point ->
                        writer.write("${dateFormat.format(point.timestamp)},")
                        writer.write("${String.format("%.6f", point.latitude)},")
                        writer.write("${String.format("%.6f", point.longitude)},")
                        writer.write("${point.altitude?.let { String.format("%.2f", it) } ?: "N/A"},")
                        writer.write("${String.format("%.2f", point.speed)},")
                        writer.write("${point.accuracy?.let { String.format("%.2f", it) } ?: "N/A"},")
                        writer.write("${point.bearing?.let { String.format("%.2f", it) } ?: "N/A"},")
                        writer.write("${point.isMoving}\n")
                    }
                    writer.write("\n")
                }
            }
            
            writer.flush()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get file extension for export format
     */
    fun getFileExtension(format: ExportFormat): String {
        return when (format) {
            ExportFormat.CSV -> ".csv"
            ExportFormat.JSON -> ".json"
        }
    }
    
    /**
     * Get MIME type for export format
     */
    fun getMimeType(format: ExportFormat): String {
        return when (format) {
            ExportFormat.CSV -> "text/csv"
            ExportFormat.JSON -> "application/json"
        }
    }
    
    /**
     * Generate filename for export
     */
    fun generateFilename(trip: Trip? = null, format: ExportFormat): String {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return if (trip != null) {
            "trip_${trip.id}_${timestamp}${getFileExtension(format)}"
        } else {
            "trips_export_${timestamp}${getFileExtension(format)}"
        }
    }
}

/**
 * Supported export formats
 */
enum class ExportFormat {
    CSV,
    JSON
}
