package com.bismark.currency.ui.converter

import androidx.lifecycle.ViewModel
import com.bismark.currency.core.Either
import com.bismark.currency.data.database.entities.ConversionResultEntity
import com.bismark.currency.domain.ConversionRateRepository
import com.bismark.currency.ui.converter.state.ConversionRateState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class CurrencyConverterViewModel(
    val conversionRateRepository: ConversionRateRepository,
    val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _conversionRate = MutableStateFlow<ConversionRateState>(ConversionRateState.Loading)
    val conversionRate = _conversionRate.asStateFlow()

    fun fetchConversionRate(from: String, to: String, amount: Long) {
        conversionRateRepository.fetchConversionRate(from = from, to = to, amount = amount)
            .map { result ->
                when (result) {
                    is Either.Right -> _conversionRate.value = ConversionRateState.Success(data = result.b)
                    is Either.Left -> _conversionRate.value = ConversionRateState.Error(message = result.a.getFailureMessage() ?: "Unknown Error")
                }
            }
            .flowOn(dispatcher)

    }
}
