package com.bismark.currency.domain

import com.bismark.currency.core.Either
import com.bismark.currency.core.Failure
import com.bismark.currency.data.database.entities.ConversionResultEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface ConversionRateRepository {

    suspend fun fetchConversionRate(from: String, to: String, amount: Long): Flow<Either<Failure, ConversionResultEntity>>
    suspend fun fetchLastThreeDaysHistory(startDate: Long, endDate: Long): Flow<Either<Failure,List<ConversionResultEntity>>>
}
