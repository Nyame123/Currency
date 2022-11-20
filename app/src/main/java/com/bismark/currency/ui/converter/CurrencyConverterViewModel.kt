package com.bismark.currency.ui.converter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bismark.currency.core.Either
import com.bismark.currency.data.rest.ConversionResultRaw
import com.bismark.currency.di.AnnotatedDispatchers
import com.bismark.currency.di.CurrencyDispatcher
import com.bismark.currency.domain.ConversionRateRepository
import com.bismark.currency.popularCurrencies
import com.bismark.currency.ui.converter.state.ConversionRateState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.jar.Pack200.Packer.LATEST
import javax.inject.Inject

@HiltViewModel
class CurrencyConverterViewModel @Inject constructor(
    private val conversionRateRepository: ConversionRateRepository,
    @AnnotatedDispatchers(CurrencyDispatcher.DEFAULT) private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _conversionRate = MutableStateFlow<ConversionRateState>(ConversionRateState.Loading)
    val conversionRate = _conversionRate.asStateFlow()

    val fromCurrencySelected = MutableStateFlow("")
    val toCurrencySelected = MutableStateFlow("")
    val fromAmount = MutableStateFlow(1.0)
    val toAmount = MutableStateFlow(0.0)

    var conversionRateInfo: ConversionResultRaw? = null
    var baseCurrency: String = ""
    var toCurrencyPosition: Int = 0
    var fromCurrencyPosition: Int = 0

    init {
        triggerCurrencyFetchOnCurrencySelected()
        triggerToAmountChanges()
        triggerFromAmountChanges()
    }

    fun onFromAmountChanges(newValue: CharSequence) {
        fromAmount.value = newValue.toString().toDouble()
    }

    fun onToAmountChanges(newValue: CharSequence) {
        toAmount.value = newValue.toString().toDouble()
    }

    private fun triggerToAmountChanges() {
        toAmount.debounce(300L)
            .onEach { amount ->
                toAmount.update { amount }
            }.launchIn(viewModelScope)
    }

    private fun triggerFromAmountChanges() {
        fromAmount.debounce(300L)
            .onEach { amount ->
                fromAmount.update { amount }
            }.launchIn(viewModelScope)
    }

    private fun triggerCurrencyFetchOnCurrencySelected() {
        fromCurrencySelected.combine(toCurrencySelected) { from, to ->
            if (from.isNotEmpty() && to.isNotEmpty()) {
                baseCurrency = from
                fetchLatestConverstionRate(from, to)
            }
        }
            .distinctUntilChanged()
            .launchIn(viewModelScope)
    }

    private fun fetchConversionRate(url: String, base: String, symbols: String) {
        viewModelScope.launch {
            conversionRateRepository.fetchConversionRate(url = url, base = base, symbols = symbols)
                .map { result ->
                    when (result) {
                        is Either.Right -> ConversionRateState.Success(data = result.b)
                        is Either.Left -> ConversionRateState.Error(
                            message = result.a.getFailureMessage() ?: "Unknown Error"
                        )
                    }
                }
                .flowOn(dispatcher)
                .catch {
                    _conversionRate.value = ConversionRateState.Error(message = "Exception thrown")
                }.collectLatest {
                    _conversionRate.value = it
                    if (it is ConversionRateState.Success) {
                        conversionRateInfo = it.data
                    }
                }
        }

    }

    private fun fetchLatestConverstionRate(from: String, to: String) {
        val symbols = popularCurrencies.apply { add(to) }
        fetchConversionRate(url = LATEST, base = from, symbols = symbols.joinToString { it })
    }
}
