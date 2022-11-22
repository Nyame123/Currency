package com.bismark.currency.data.datasource

import com.bismark.currency.core.Either
import com.bismark.currency.core.Failure
import com.bismark.currency.data.rest.ApiService
import com.bismark.currency.data.rest.ConversionResultRaw
import javax.inject.Inject

class RemoteDataSourceImpl @Inject constructor(private val apiService: ApiService) : RemoteDataSource {

    override suspend fun fetchConversionRate(
        url: String,
        base: String,
        symbols: String
    ): Either<Failure, ConversionResultRaw> {
        val response = apiService.getLatestConversionRate(url = url, base = base, symbols = symbols)
        return if (response.success == true) {
            Either.Right(response)
        } else {
            Either.Left(Failure.ServerConnectionError(Exception(response.errorBody?.info)))
        }
    }

}
