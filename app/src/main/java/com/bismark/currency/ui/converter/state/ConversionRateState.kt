package com.bismark.currency.ui.converter.state

import com.bismark.currency.data.rest.ConversionResultRaw

sealed interface ConversionRateState{
    object Loading: ConversionRateState
    data class Success(val data: ConversionResultRaw): ConversionRateState
    data class Error(val message: String): ConversionRateState
}
