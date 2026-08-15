package com.example.demo.database

import androidx.room.TypeConverter
import java.util.Date

class TimestampConverter {

    @TypeConverter
    fun fromTimestamp(value: Long): Date {
        return Date(value)
    }

    @TypeConverter
    fun toTimestamp(date: Date): Long {
        return date.time
    }
}
