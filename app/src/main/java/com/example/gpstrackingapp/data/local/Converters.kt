package com.example.gpstrackingapp.data.local

import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.util.*

/**
 * Room database type converters for custom data types
 */
class Converters {
    private val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    
    @TypeConverter
    fun fromTimestamp(value: String?): Date? {
        return value?.let { 
            try {
                formatter.parse(it)
            } catch (e: Exception) {
                null
            }
        }
    }
    
    @TypeConverter
    fun dateToTimestamp(date: Date?): String? {
        return date?.let { formatter.format(it) }
    }
}
