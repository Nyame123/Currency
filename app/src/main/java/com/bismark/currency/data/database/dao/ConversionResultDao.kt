package com.bismark.currency.data.database.dao

import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.bismark.currency.data.database.entities.ConversionResultEntity

interface ConversionResultDao {

    @Insert(onConflict = REPLACE)
    suspend fun insertConversionResult(conversionResultEntity: ConversionResultEntity)

    @Query("Select * from conversion where date IN (:dateRange)")
    suspend fun getLastThreeDaysHistory(dateRange: List<String>): List<ConversionResultEntity>
}
