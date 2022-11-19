package com.bismark.currency.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bismark.currency.data.database.dao.ConversionResultDao
import com.bismark.currency.data.database.entities.ConversionResultEntity

const val DATABASE_NAME = "conversion_database"
@Database(entities = [ConversionResultEntity::class], version = 1, exportSchema = false)
abstract class ConversionDatabase : RoomDatabase() {
    abstract fun conversionResultDao(): ConversionResultDao
}
