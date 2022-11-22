package com.bismark.currency

import app.cash.turbine.test
import com.bismark.currency.core.Either
import com.bismark.currency.core.Failure
import com.bismark.currency.core.leftOrNull
import com.bismark.currency.core.rightOrNull
import com.bismark.currency.data.ConversionRateRepositoryImpl
import com.bismark.currency.data.datasource.RemoteDataSource
import com.bismark.currency.data.rest.ConversionResultRaw
import com.bismark.currency.provider.provideConversionRateSuccess
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ConversionRateRepositoryImplTest {

    @get: Rule
    val coroutineTestRule = CoroutineTestRule(testDispatcher = StandardTestDispatcher())

    private val URL = "endpoint/"
    private val BASE = "EGP"
    private val SYMBOL = "USD"

    private val remoteDataSourceMock: RemoteDataSource = mockk(relaxed = true)
    lateinit var conversionRateRepositoryImpl: ConversionRateRepositoryImpl

    @Before
    fun setUp() {
        conversionRateRepositoryImpl = ConversionRateRepositoryImpl(remoteDataSource = remoteDataSourceMock)
    }

    @Test
    fun `given remote datasource when the response is not successful return Either Left of the error`() = runTest {

        val info = "Error fetching the Conversion info"
        val errorMessage = Failure.ServerConnectionError(Throwable(info))

        val result = Either.Left<Failure>(a = errorMessage)

        coEvery { remoteDataSourceMock.fetchConversionRate(url = URL, base = BASE, symbols = SYMBOL) } returns result

        conversionRateRepositoryImpl.fetchConversionRate(url = URL, base = BASE, symbols = SYMBOL).test {
            val response = awaitItem()
            assertTrue(response.isLeft)
            assertEquals(response.leftOrNull()?.getFailureMessage(), info)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `given remote datasource when the response is successful return Either Right of the object`() = runTest {

        val mockedSuccess = provideConversionRateSuccess(
            success = true,
            date = "2022-11-21",
            timestamp = 1669030923,
            base = BASE,
            symbols = mutableListOf<String>().apply { add(SYMBOL) }
        )

        val result = Either.Right(b = mockedSuccess)

        coEvery { remoteDataSourceMock.fetchConversionRate(url = URL, base = BASE, symbols = SYMBOL) } returns result

        conversionRateRepositoryImpl.fetchConversionRate(url = URL, base = BASE, symbols = SYMBOL).test {
            val response = awaitItem()
            assertTrue(response.isRight)
            assertEquals(response.rightOrNull(), mockedSuccess)
            cancelAndConsumeRemainingEvents()
        }
    }

}
