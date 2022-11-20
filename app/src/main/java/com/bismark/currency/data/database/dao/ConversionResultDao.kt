package com.bismark.currency.data.database.dao

import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.bismark.currency.data.database.entities.ConversionResultEntity

interface ConversionResultDao {

    @Insert(onConflict = REPLACE)
    suspend fun insertConversionResult(conversionResultEntity: ConversionResultEntity): Long

    @Query("Select * from conversion where date BETWEEN :startDate and :endDate")
    suspend fun getLastThreeDaysHistory(startDate: Long, endDate: Long): List<ConversionResultEntity>
}
