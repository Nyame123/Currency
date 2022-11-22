package com.bismark.currency.provider

import com.bismark.currency.data.rest.ConversionResultRaw
import com.bismark.currency.extensions.empty

fun provideConversionRateSuccess(
    success: Boolean = true,
    date: String = String.empty(),
    timestamp: Long = 0L,
    base: String = String.empty(),
    symbols: List<String>
) = ConversionResultRaw(
    success = success,
    date = date,
    timestamp = timestamp,
    base = base,
    rates = mutableMapOf<String, Double>().apply {
        symbols.forEach {
            put(it, 1.845498)
        }

    },
    errorBody = null
)
