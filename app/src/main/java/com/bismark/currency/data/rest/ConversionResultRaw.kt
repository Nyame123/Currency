package com.bismark.currency.data.rest

import com.bismark.currency.extensions.empty
import com.google.gson.annotations.SerializedName

/**
 * Conversion Result object
 **/
data class ConversionResultRaw(
    internal val success: Boolean? = false,
    internal val date: String? = String.empty(),
    internal val timestamp: Long? = 0L,
    internal val base: String? = String.empty(),
    internal val rates: Map<String, Double>? = null,
   @SerializedName("error") internal val errorBody: ErrorRaw?
)

/**
 * Information about the Error object
 **/
data class ErrorRaw(
    internal val code: Long? = 0L,
    internal val info: String? = String.empty()
)
