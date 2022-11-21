package com.bismark.currency.ui.converter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bismark.currency.core.Either
import com.bismark.currency.core.Failure
import com.bismark.currency.data.rest.ConversionResultRaw
import com.bismark.currency.di.AnnotatedDispatchers
import com.bismark.currency.di.CurrencyDispatcher
import com.bismark.currency.domain.ConversionRateRepository
import com.bismark.currency.popularCurrencies
import com.bismark.currency.ui.converter.state.ConversionRateState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import java.util.jar.Pack200.Packer.LATEST
import javax.inject.Inject

@HiltViewModel
class CurrencyConverterViewModel @Inject constructor(
    private val conversionRateRepository: ConversionRateRepository,
    @AnnotatedDispatchers(CurrencyDispatcher.IO) private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _conversionRate = MutableStateFlow<ConversionRateState?>(null)
    val conversionRate = _conversionRate.asStateFlow()

    private val _historyRateOne = MutableStateFlow<ConversionRateState>(ConversionRateState.Loading)
    val historyRateOne = _historyRateOne.asStateFlow()
    private val _historyRateTwo = MutableStateFlow<ConversionRateState>(ConversionRateState.Loading)
    val historyRateTwo = _historyRateTwo.asStateFlow()
    private val _historyRateThree = MutableStateFlow<ConversionRateState>(ConversionRateState.Loading)
    val historyRateThree = _historyRateThree.asStateFlow()

    val fromCurrencySelected = MutableStateFlow("")
    val toCurrencySelected = MutableStateFlow("")

    var conversionRateInfo: ConversionResultRaw? = null
    var baseCurrency: String = ""
    var toCurrency: String = ""
    var toCurrencyPosition: Int = 0
    var fromCurrencyPosition: Int = 0

    init {
        triggerCurrencyFetchOnCurrencySelected()
    }

    private fun triggerCurrencyFetchOnCurrencySelected() {
        fromCurrencySelected.combine(toCurrencySelected) { from, to ->
            if (from.isNotEmpty() && to.isNotEmpty() && from != to) {
                baseCurrency = from
                toCurrency = to
                fetchLatestConversionRate(from, to)
            }
        }.launchIn(viewModelScope)
    }

    private fun fetchConversionRate(url: String, base: String, symbols: String) {
        viewModelScope.launch {
            conversionRateRepository.fetchConversionRate(url = url, base = base, symbols = symbols)
                .onStart {
                    _conversionRate.value = ConversionRateState.Loading
                }
                .map { result ->
                    when (result) {
                        is Either.Right -> ConversionRateState.Success(data = result.b)
                        is Either.Left -> ConversionRateState.Error(
                            message = result.a.getFailureMessage()
                        )
                    }
                }
                .flowOn(dispatcher)
                .catch {
                    _conversionRate.value = ConversionRateState.Error(message = "Exception thrown from Flow upstream")
                }.collectLatest {
                    _conversionRate.value = it
                    if (it is ConversionRateState.Success) {
                        conversionRateInfo = it.data
                    }
                }
        }
    }

    fun fetchHistoricalRate(urls: List<String>, base: String, symbols: String) {
        val historicalConversionRateOne: Flow<Either<Failure, ConversionResultRaw>> =
            conversionRateRepository.fetchConversionRate(url = urls[0], base = base, symbols = symbols)
        val historicalConversionRateTwo: Flow<Either<Failure, ConversionResultRaw>> =
            conversionRateRepository.fetchConversionRate(url = urls[1], base = base, symbols = symbols)
        val historicalConversionRateThree: Flow<Either<Failure, ConversionResultRaw>> =
            conversionRateRepository.fetchConversionRate(url = urls[2], base = base, symbols = symbols)
        viewModelScope.launch {
            historicalConversionRateOne.zip(historicalConversionRateTwo) { historyOne, historyTwo ->
                Pair(historyOne, historyTwo)
            }.zip(historicalConversionRateThree) { pair, historyThree ->
                Pair(pair, historyThree)
            }.flowOn(dispatcher)
                .collectLatest {triple ->
                    //first historical response
                    when (val first = triple.first.first) {
                        is Either.Right -> _historyRateOne.value = ConversionRateState.Success(data = first.b)
                        is Either.Left ->  _historyRateOne.value = ConversionRateState.Error(
                            message = first.a.getFailureMessage() ?: "Unknown Error"
                        )
                    }

                    //second historical response
                    when (val second = triple.first.second) {
                        is Either.Right -> _historyRateTwo.value = ConversionRateState.Success(data = second.b)
                        is Either.Left -> _historyRateTwo.value = ConversionRateState.Error(
                            message = second.a.getFailureMessage() ?: "Unknown Error"
                        )
                    }

                    //third historical response
                    when (val third = triple.second) {
                        is Either.Right -> _historyRateThree.value = ConversionRateState.Success(data = third.b)
                        is Either.Left -> _historyRateThree.value = ConversionRateState.Error(
                            message = third.a.getFailureMessage() ?: "Unknown Error"
                        )
                    }

            }
        }
    }

    private fun fetchLatestConversionRate(from: String, to: String) {
        val symbols = popularCurrencies.apply { add(to) }
        fetchConversionRate(url = LATEST, base = from, symbols = symbols.joinToString { it })
    }
}
