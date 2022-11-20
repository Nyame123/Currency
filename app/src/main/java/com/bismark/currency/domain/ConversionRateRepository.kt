package com.bismark.currency.domain

import com.bismark.currency.core.Either
import com.bismark.currency.core.Failure
import com.bismark.currency.data.rest.ConversionResultRaw
import kotlinx.coroutines.flow.Flow

interface ConversionRateRepository {

    fun fetchConversionRate(url: String, base: String, symbols: String): Flow<Either<Failure, ConversionResultRaw>>
}
