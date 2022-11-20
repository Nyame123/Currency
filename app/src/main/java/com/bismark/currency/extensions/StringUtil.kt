package com.bismark.currency.extensions

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Returns empty String with no characters
 **/
fun String.Companion.empty(): String = ""

/**
 * Return the receiver or the empty String if the receiver is `null`
 **/
fun String?.toSafeString(): String {
    if (this == null) return String.empty()
    return toString()
}

fun String.toDate(): Date? = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(this)

