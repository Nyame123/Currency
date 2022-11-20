package com.bismark.currency.data.datasource

import com.bismark.currency.core.Either
import com.bismark.currency.core.Failure
import com.bismark.currency.data.datasource.RemoteDataSource
import com.bismark.currency.data.rest.ApiService
import com.bismark.currency.data.rest.ConversionResultRaw

class RemoteDataSourceImpl(private val apiService: ApiService) : RemoteDataSource {

    override suspend fun fetchConversionRate(from: String, to: String, amount: Long): Either<Failure, ConversionResultRaw> {
        val response = apiService.getConversionRate(from = from, to = to, amount = amount.toString())
        return if (response.isSuccessful) {
            val body = response.body()
            if (body?.success == true) {
                Either.Right(body)
            } else {
                Either.Left(Failure.ServerConnectionError(Exception(body?.errorBody?.info)))
            }
        } else {
            Either.Left(Failure.ServerConnectionError(Exception(response.message())))
        }
    }

}
