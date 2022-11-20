package com.bismark.currency.data.datasource.local

import com.bismark.currency.core.Either
import com.bismark.currency.core.Failure
import com.bismark.currency.data.database.entities.ConversionResultEntity

interface LocalDataSource{
    suspend fun insertConversionRate(conversionResultEntity: ConversionResultEntity): Either<Failure,Long>

    suspend fun fetchLastThreeDaysHistory(startDate: Long, endDate: Long): Either<Failure,List<ConversionResultEntity>>
}
