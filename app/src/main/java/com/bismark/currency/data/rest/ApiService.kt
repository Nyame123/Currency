package com.bismark.currency.data.rest

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiService {
    companion object {

        const val BASE_URL = "https://api.apilayer.com/fixer/"
        const val LATEST = "latest"
        private const val ACCESS_KEY = "apikey"
        private const val ACCESS_VALUE = "ilEY0zFZqp1XuKoC4bpnI8XfB1JSn5AA"
        private const val BASE = "base"
        private const val SYMBOL = "symbols"
    }

    @GET
    suspend fun getLatestConversionRate(
        @Url url: String,
        @Query(ACCESS_KEY) accessKey: String = ACCESS_VALUE,
        @Query(BASE) base: String,
        @Query(SYMBOL) symbols: String,
    ): ConversionResultRaw
}
