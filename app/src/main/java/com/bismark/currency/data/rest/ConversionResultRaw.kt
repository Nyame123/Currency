package com.bismark.currency.data.rest

import com.bismark.currency.data.database.entities.ConversionResultEntity
import com.bismark.currency.extensions.empty
import com.bismark.currency.extensions.toDate
import com.bismark.currency.extensions.toSafeString

/**
 * Conversion Result object
 **/
data class ConversionResultRaw(
    internal val date: String? = String.empty(),
    internal val info: InfoRaw?,
    internal val query: QueryRaw?,
    internal val errorBody: ErrorRaw?,
    internal val success: Boolean? = false
) {

    fun asEntity() = ConversionResultEntity(
        date = date?.toDate(),
        rate = info?.rate ?: 0L,
        timestamp = info?.timestamp ?: 0L,
        from = query?.from.toSafeString(),
        to = query?.to.toSafeString(),
        amount = query?.amount ?: 0L,
        success = success ?: false
    )
}

/**
 * Information about the Error object
 **/
data class ErrorRaw(
    internal val code: Long? = 0L,
    internal val info: String? = String.empty()
)

/**
 * Information about the new rate and timestamp
 * between `to` and `from` currencies
 **/
data class InfoRaw(
    internal val rate: Long? = 0L,
    internal val timestamp: Long
)

/**
 * Query information object
 **/
data class QueryRaw(
    internal val amount: Long? = 0L,
    internal val from: String? = String.empty(),
    internal val to: String? = String.empty(),
)
