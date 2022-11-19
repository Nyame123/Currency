package com.bismark.currency.data.rest

import com.bismark.currency.extensions.empty

/**
 * Conversion Result object
 **/
data class ConversionResultRaw(
    internal val date: String? = String.empty(),
    internal val info: Info?,
    internal val query: Query?,
    internal val success: Boolean? = false
)

/**
 * Information about the new rate and timestamp
 * between `to` and `from` currencies
 **/
data class Info(
  internal val rate: String? = String.empty(),
  internal val timestamp: Long
)

/**
 * Query information object
 **/
data class Query(
    internal val amount: String? = String.empty(),
    internal val from: String? = String.empty(),
    internal val to: String? = String.empty(),
)
