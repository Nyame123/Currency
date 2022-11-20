package com.bismark.currency.data.datasource.local

import com.bismark.currency.core.Either
import com.bismark.currency.core.Failure
import com.bismark.currency.core.IDatabaseRequest
import com.bismark.currency.data.database.ConversionDatabase
import com.bismark.currency.data.database.entities.ConversionResultEntity

class LocalDataSourceImpl(private val conversionDatabase: ConversionDatabase) : LocalDataSource, IDatabaseRequest {

    override suspend fun insertConversionRate(conversionResultEntity: ConversionResultEntity): Either<Failure, Long> = onSuspendCall {
        conversionDatabase.conversionResultDao().insertConversionResult(conversionResultEntity)
    }


    override suspend fun fetchLastThreeDaysHistory(startDate: Long, endDate: Long): Either<Failure, List<ConversionResultEntity>> = onSuspendCall {
       conversionDatabase.conversionResultDao().getLastThreeDaysHistory(startDate,endDate)
    }
}
