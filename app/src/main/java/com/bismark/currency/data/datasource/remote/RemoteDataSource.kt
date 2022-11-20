package com.bismark.currency.data.datasource.remote

import com.bismark.currency.core.Either
import com.bismark.currency.core.Failure
import com.bismark.currency.data.rest.ConversionResultRaw

interface RemoteDataSource {
    suspend fun fetchConversionRate(from: String, to: String, amount: Long): Either<Failure,ConversionResultRaw>
}
