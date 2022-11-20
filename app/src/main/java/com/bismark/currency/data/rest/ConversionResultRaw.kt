package com.bismark.currency.data.rest

import com.bismark.currency.extensions.empty

/**
 * Conversion Result object
 **/
data class ConversionResultRaw(
    internal val success: Boolean? = false,
    internal val date: String? = String.empty(),
    internal val timestamp: Long?,
    internal val base: String? = String.empty(),
    internal val rates: Map<String, Double>?,
    internal val errorBody: ErrorRaw?
)

/**
 * Information about the Error object
 **/
data class ErrorRaw(
    internal val code: Long? = 0L,
    internal val info: String? = String.empty()
)
