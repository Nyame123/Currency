package com.bismark.currency.core

sealed class Failure {

    data class NetworkConnection(val exception: Exception? = null) : Failure()
    class ServerConnectionError(val exception: Throwable? = null) : Failure()
    class DatabaseError(val exception: Throwable) : Failure()
    class Error(val status: Int, val message: String?, val code: String = "") : Failure()
    class FlowError(val exception: Throwable) : Failure()

    /**
     * An ext function for the [Failure] that allows to get an error message from [Failure] if any
     */
    fun getFailureMessage(): String? = when (this) {
        is Error -> "$status : $message"
        is FlowError -> exception.message
        is DatabaseError -> exception.message
        is NetworkConnection -> exception?.message ?: "NetworkConnection"
        is ServerConnectionError -> exception?.message ?: "ServerConnectionError"
    }

    /**
     * Call this for safe typeCasting to [Failure.Error] and getting the message property or null
     */
    fun getErrorMessageOrNull(): String? = (this as? Error)?.message
}
