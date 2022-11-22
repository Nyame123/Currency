package com.bismark.currency.core

sealed class Failure {

    class ServerConnectionError(val exception: Throwable? = null) : Failure()

    /**
     * An ext function for the [Failure] that allows to get an error message from [Failure] if any
     */
    fun getFailureMessage(): String = when (this) {
        is ServerConnectionError -> exception?.message ?: "ServerConnectionError"
    }
}
