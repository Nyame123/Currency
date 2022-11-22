package com.bismark.currency

import app.cash.turbine.test
import com.bismark.currency.core.Either
import com.bismark.currency.core.Failure
import com.bismark.currency.domain.ConversionRateRepository
import com.bismark.currency.domain.usecase.NetworkConnectivity
import com.bismark.currency.extensions.INTERNET_CONNECTIVITY_ERROR
import com.bismark.currency.provider.provideConversionRateSuccess
import com.bismark.currency.ui.converter.CurrencyConverterViewModel
import com.bismark.currency.ui.converter.state.ConversionRateState
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CurrencyConverterViewModelTest {

    @get: Rule
    val coroutineTestRule = CoroutineTestRule(testDispatcher = StandardTestDispatcher())

    private val conversionRateRepositoryMock: ConversionRateRepository = mockk(relaxed = true)
    private val networkConnectivityMock: NetworkConnectivity = mockk(relaxed = true)

    lateinit var currencyConverterViewModel: CurrencyConverterViewModel

    private val BASE = "EGP"
    private val SYMBOL = "USD"

    @Before
    fun setUp() {
        currencyConverterViewModel = CurrencyConverterViewModel(
            conversionRateRepository = conversionRateRepositoryMock,
            connectivity = networkConnectivityMock
        )
    }

    @Test
    fun `given no internet when fetch latest conversion rate, display no internet connection message`() = runTest {
        val fromCurrencySelected = BASE
        val toCurrencySelected = SYMBOL

        val expected = INTERNET_CONNECTIVITY_ERROR

        every { networkConnectivityMock.isConnected() } returns false

        currencyConverterViewModel.fetchLatestConversionRate(fromCurrencySelected, toCurrencySelected)

        assertEquals((currencyConverterViewModel.conversionRate.value as ConversionRateState.Error).message, expected)
    }

    @Test
    fun `given no internet when fetching last three historical conversion rate, display no internet connection message`() =
        runTest {

            val expected = INTERNET_CONNECTIVITY_ERROR

            every { networkConnectivityMock.isConnected() } returns false

            currencyConverterViewModel.fetchHistoricalRate(urls = mutableListOf(), base = BASE, symbols = SYMBOL)

            assertEquals((currencyConverterViewModel.historyRateOne.value as ConversionRateState.Error).message, expected)
            assertEquals((currencyConverterViewModel.historyRateTwo.value as ConversionRateState.Error).message, expected)
            assertEquals((currencyConverterViewModel.historyRateThree.value as ConversionRateState.Error).message, expected)
        }

    @Test
    fun `given internet when fetching last three historical conversion rate, display various historical rates`() =
        runTest {

            val firstUrl = "2022-12-03"
            val secondUrl = "2022-12-02"
            val thirdUrl = "2022-12-01"
            val urls = mutableListOf<String>().apply {
                add(firstUrl); add(secondUrl); add(thirdUrl)
            }

            val firstMockedSuccess = provideConversionRateSuccess(
                date = firstUrl,
                timestamp = 1669030923,
                base = BASE,
                symbols = mutableListOf<String>().apply { add(SYMBOL) }
            )

            val secondMockedSuccess = provideConversionRateSuccess(
                date = secondUrl,
                timestamp = 1669030923,
                base = BASE,
                symbols = mutableListOf<String>().apply { add(SYMBOL) }
            )

            val thirdMockedSuccess = provideConversionRateSuccess(
                date = thirdUrl,
                timestamp = 1669030923,
                base = BASE,
                symbols = mutableListOf<String>().apply { add(SYMBOL) }
            )


            every { networkConnectivityMock.isConnected() } returns true
            coEvery {
                conversionRateRepositoryMock.fetchConversionRate(
                    url = firstUrl,
                    base = BASE,
                    symbols = SYMBOL
                )
            } returns
                    flow { emit(Either.Right(firstMockedSuccess)) }
            coEvery {
                conversionRateRepositoryMock.fetchConversionRate(
                    url = secondUrl,
                    base = BASE,
                    symbols = SYMBOL
                )
            } returns
                    flow { emit(Either.Right(secondMockedSuccess)) }
            coEvery {
                conversionRateRepositoryMock.fetchConversionRate(
                    url = thirdUrl,
                    base = BASE,
                    symbols = SYMBOL
                )
            } returns
                    flow { emit(Either.Right(thirdMockedSuccess)) }

            currencyConverterViewModel.historyRateOne.test {
                currencyConverterViewModel.fetchHistoricalRate(urls = urls, base = BASE, symbols = SYMBOL)
                assertTrue(awaitItem() is ConversionRateState.Loading)
                assertTrue(awaitItem() is ConversionRateState.Success)
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `given internet when fetching last three historical conversion rate and returns error, display error`() =
        runTest {

            val firstUrl = "2022-12-03"
            val secondUrl = "2022-12-02"
            val thirdUrl = "2022-12-01"
            val urls = mutableListOf<String>().apply {
                add(firstUrl); add(secondUrl); add(thirdUrl)
            }

            val info = "Error fetching the Conversion info"
            val errorMessage = Failure.ServerConnectionError(Throwable(info))


            every { networkConnectivityMock.isConnected() } returns true
            coEvery {
                conversionRateRepositoryMock.fetchConversionRate(
                    url = firstUrl,
                    base = BASE,
                    symbols = SYMBOL
                )
            } returns
                    flow { emit(Either.Left<Failure>(errorMessage)) }
            coEvery {
                conversionRateRepositoryMock.fetchConversionRate(
                    url = secondUrl,
                    base = BASE,
                    symbols = SYMBOL
                )
            } returns
                    flow { emit(Either.Left<Failure>(errorMessage)) }
            coEvery {
                conversionRateRepositoryMock.fetchConversionRate(
                    url = thirdUrl,
                    base = BASE,
                    symbols = SYMBOL
                )
            } returns
                    flow { emit(Either.Left<Failure>(errorMessage)) }

            currencyConverterViewModel.historyRateOne.test {
                currencyConverterViewModel.fetchHistoricalRate(urls = urls, base = BASE, symbols = SYMBOL)
                assertTrue(awaitItem() is ConversionRateState.Loading)
                assertTrue(awaitItem() is ConversionRateState.Error)
                cancelAndConsumeRemainingEvents()
            }
        }
}
