package com.bismark.currency.data.rest

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    companion object {

        const val BASE_URL = "https://data.fixer.io/api/"
        private const val CONVERT = "convert"
        private const val ACCESS_KEY = "access_key"
        private const val ACCESS_VALUE = "sZ4IS8QZ1ekB56IFwDZOki0usvUzFCZr"
        private const val FROM = "from"
        private const val TO = "to"
        private const val AMOUNT = "amount"
    }

    @GET(CONVERT)
    suspend fun getConversionRate(
        @Query(ACCESS_KEY) accessKey: String = ACCESS_VALUE,
        @Query(FROM) from: String,
        @Query(TO) to: String,
        @Query(AMOUNT) amount: String,
    ): Response<ConversionResultRaw>
}