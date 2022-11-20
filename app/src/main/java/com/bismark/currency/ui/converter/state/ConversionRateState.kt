package com.bismark.currency.ui.converter.state

import com.bismark.currency.data.database.entities.ConversionResultEntity

sealed interface ConversionRateState{
    object Loading: ConversionRateState
    data class Success(val data: ConversionResultEntity): ConversionRateState
    data class Error(val message: String): ConversionRateState
}
