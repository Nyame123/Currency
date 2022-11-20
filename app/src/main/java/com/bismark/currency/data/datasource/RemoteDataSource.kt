package com.bismark.currency.data.datasource

import com.bismark.currency.core.Either
import com.bismark.currency.core.Failure
import com.bismark.currency.data.rest.ConversionResultRaw

interface RemoteDataSource {
    suspend fun fetchConversionRate(url: String, base: String, symbols: String): Either<Failure,ConversionResultRaw>
}
