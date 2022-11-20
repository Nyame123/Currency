package com.bismark.currency.data.database.converter

import androidx.room.TypeConverter
import java.util.Date

class DateConverter {

    @TypeConverter
    fun toDate(dateValue: Long?): Date?{
        return dateValue?.let { Date(it) }
    }

    @TypeConverter
    fun toLong(date: Date?): Long? = date?.time

}
