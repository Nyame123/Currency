package com.bismark.currency.ui.converter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bismark.currency.core.Either
import com.bismark.currency.core.Failure
import com.bismark.currency.data.rest.ApiService
import com.bismark.currency.data.rest.ConversionResultRaw
import com.bismark.currency.di.AnnotatedDispatchers
import com.bismark.currency.di.CurrencyDispatcher
import com.bismark.currency.domain.ConversionRateRepository
import com.bismark.currency.domain.usecase.NetworkConnectivity
import com.bismark.currency.extensions.FLOW_CONNECTION_ERROR
import com.bismark.currency.extensions.INTERNET_CONNECTIVITY_ERROR
import com.bismark.currency.popularCurrencies
import com.bismark.currency.ui.converter.state.ConversionRateState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
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
import javax.inject.Inject

@HiltViewModel
class CurrencyConverterViewModel @Inject constructor(
    private val conversionRateRepository: ConversionRateRepository,
    private val connectivity: NetworkConnectivity,
    @AnnotatedDispatchers(CurrencyDispatcher.IO) private val dispatcher: CoroutineDispatcher = Dispatchers.Default
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
               // .flowOn(dispatcher)
                .catch {
                    _conversionRate.value = ConversionRateState.Error(message = FLOW_CONNECTION_ERROR)
                }.collectLatest {
                    _conversionRate.value = it
                    if (it is ConversionRateState.Success) {
                        conversionRateInfo = it.data
                    }
                }
        }
    }

    fun fetchHistoricalRate(urls: List<String>, base: String, symbols: String) {
        if (connectivity.isConnected()) {
            val historicalConversionRateOne: Flow<Either<Failure, ConversionResultRaw>> =
                conversionRateRepository.fetchConversionRate(url = urls[0], base = base, symbols = symbols).onStart {
                    _historyRateOne.value = ConversionRateState.Loading
                }
            val historicalConversionRateTwo: Flow<Either<Failure, ConversionResultRaw>> =
                conversionRateRepository.fetchConversionRate(url = urls[1], base = base, symbols = symbols).onStart {
                    _historyRateTwo.value = ConversionRateState.Loading
                }
            val historicalConversionRateThree: Flow<Either<Failure, ConversionResultRaw>> =
                conversionRateRepository.fetchConversionRate(url = urls[2], base = base, symbols = symbols).onStart {
                    _historyRateThree.value = ConversionRateState.Loading
                }
            viewModelScope.launch {
                historicalFlow(historicalConversionRateOne, historicalConversionRateTwo, historicalConversionRateThree)
                    .flowOn(dispatcher)
                    .collectLatest { triple ->
                        //first historical response
                        handleFirstHistoricalState(triple)

                        //second historical response
                        handleSecondHistoricalState(triple)

                        //third historical response
                        handleThirdHistoricalState(triple)
                    }
            }
        } else {
            _historyRateOne.value = ConversionRateState.Error(message = INTERNET_CONNECTIVITY_ERROR)
            _historyRateTwo.value = ConversionRateState.Error(message = INTERNET_CONNECTIVITY_ERROR)
            _historyRateThree.value = ConversionRateState.Error(message = INTERNET_CONNECTIVITY_ERROR)
        }
    }

    fun historicalFlow(
        historicalConversionRateOne: Flow<Either<Failure, ConversionResultRaw>>,
        historicalConversionRateTwo: Flow<Either<Failure, ConversionResultRaw>>,
        historicalConversionRateThree: Flow<Either<Failure, ConversionResultRaw>>
    ) = historicalConversionRateOne.zip(historicalConversionRateTwo) { historyOne, historyTwo ->
        Pair(historyOne, historyTwo)
    }.zip(historicalConversionRateThree) { pair, historyThree ->
        Pair(pair, historyThree)
    }

    private fun handleThirdHistoricalState(triple: Pair<Pair<Either<Failure, ConversionResultRaw>, Either<Failure, ConversionResultRaw>>, Either<Failure, ConversionResultRaw>>) {
        when (val third = triple.second) {
            is Either.Right -> _historyRateThree.value = ConversionRateState.Success(data = third.b)
            is Either.Left -> _historyRateThree.value = ConversionRateState.Error(
                message = third.a.getFailureMessage()
            )
        }
    }

    private fun handleSecondHistoricalState(triple: Pair<Pair<Either<Failure, ConversionResultRaw>, Either<Failure, ConversionResultRaw>>, Either<Failure, ConversionResultRaw>>) {
        when (val second = triple.first.second) {
            is Either.Right -> _historyRateTwo.value = ConversionRateState.Success(data = second.b)
            is Either.Left -> _historyRateTwo.value = ConversionRateState.Error(
                message = second.a.getFailureMessage()
            )
        }
    }

    private fun handleFirstHistoricalState(triple: Pair<Pair<Either<Failure, ConversionResultRaw>, Either<Failure, ConversionResultRaw>>, Either<Failure, ConversionResultRaw>>) {
        when (val first = triple.first.first) {
            is Either.Right -> _historyRateOne.value = ConversionRateState.Success(data = first.b)
            is Either.Left -> _historyRateOne.value = ConversionRateState.Error(
                message = first.a.getFailureMessage()
            )
        }
    }

    fun fetchLatestConversionRate(from: String, to: String) {
        if (connectivity.isConnected()) {
            val symbols = popularCurrencies.apply { add(to) }
            fetchConversionRate(url = ApiService.LATEST, base = from, symbols = symbols.joinToString { it })
        } else {
            _conversionRate.value = ConversionRateState.Error(message = INTERNET_CONNECTIVITY_ERROR)
        }
    }
}
