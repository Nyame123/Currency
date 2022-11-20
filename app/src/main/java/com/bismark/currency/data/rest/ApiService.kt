package com.bismark.currency.data.rest

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiService {
    companion object {

        const val BASE_URL = "https://data.fixer.io/api/"
        private const val LATEST = "latest"
        private const val ACCESS_KEY = "access_key"
        private const val ACCESS_VALUE = "sZ4IS8QZ1ekB56IFwDZOki0usvUzFCZr"
        private const val BASE = "base"
        private const val SYMBOL = "symbols"
    }

    @GET
    suspend fun getLatestConversionRate(
        @Url url: String,
        @Query(ACCESS_KEY) accessKey: String = ACCESS_VALUE,
        @Query(BASE) base: String,
        @Query(SYMBOL) symbols: String,
    ): Response<ConversionResultRaw>
}
