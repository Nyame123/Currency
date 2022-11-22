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
        //return Either.Right(mocked)
        return if (response.success == true) {
            Either.Right(response)
        } else {
            Either.Left(Failure.ServerConnectionError(Exception(response.errorBody?.info)))
        }
    }

}

val mocked = ConversionResultRaw(
    success = true,
    date = "2022-11-21",
    timestamp = 1669030923,
    base = "USD",
    rates = mutableMapOf<String, Double>().apply {
        put("GBP",0.845498)
        put("GHS",0.845498)
        put("EUR",0.845498)
        put("JPY",0.1122)
        put("AUD",2.845498)
        put("CHF",2.845498)
        put("CNH",2.845498)
        put("HDK",2.845498)
        put("NZD",2.845498)
        put("EGP",2.845498)
        put("USD",1.845498)
    },
    errorBody = null
)
