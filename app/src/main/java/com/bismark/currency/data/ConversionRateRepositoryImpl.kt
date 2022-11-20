package com.bismark.currency.data

import com.bismark.currency.core.Either
import com.bismark.currency.core.Failure
import com.bismark.currency.core.onFailureSuspended
import com.bismark.currency.data.datasource.RemoteDataSource
import com.bismark.currency.data.rest.ConversionResultRaw
import com.bismark.currency.domain.ConversionRateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ConversionRateRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : ConversionRateRepository {

    override fun fetchConversionRate(
        url: String, base: String, symbols: String
    ): Flow<Either<Failure, ConversionResultRaw>> = flow {
        val conversionResultRaw =
            remoteDataSource.fetchConversionRate(url = url, base = base, symbols = symbols)
                .onFailureSuspended { failure ->
                    //emits the error when remote fetching of the conversion rate fails
                    emit(Either.Left(failure))
                }

        //Finally emits the conversion rate when fetching of the rate is successful
        emit(conversionResultRaw)
    }
}
