package com.bismark.currency.domain.usecase

interface NetworkConnectivity {
    fun isConnected(): Boolean
}
