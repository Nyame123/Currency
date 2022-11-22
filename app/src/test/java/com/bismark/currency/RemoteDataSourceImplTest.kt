package com.bismark.currency

import com.bismark.currency.core.leftOrNull
import com.bismark.currency.core.rightOrNull
import com.bismark.currency.data.datasource.RemoteDataSourceImpl
import com.bismark.currency.data.rest.ApiService
import com.bismark.currency.data.rest.ConversionResultRaw
import com.bismark.currency.data.rest.ErrorRaw
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RemoteDataSourceImplTest {

    @get: Rule
    val coroutineTestRule = CoroutineTestRule(testDispatcher = StandardTestDispatcher())

    private val apiServiceMock: ApiService = mockk(relaxed = true)
    lateinit var remoteDataSourceImpl: RemoteDataSourceImpl

    private val URL = "endpoint/"
    private val BASE = "EGP"
    private val SYMBOL = "USD"

    @Before
    fun setUp() {
        remoteDataSourceImpl = RemoteDataSourceImpl(apiService = apiServiceMock)
    }

    @Test
    fun `given api service when response is not successful throw error`() = runTest {
        val info = "Error fetching the Conversion info"
        val mockedError = ConversionResultRaw(
            success = false,
            errorBody = ErrorRaw(code = 101L, info = info)
        )

        coEvery { apiServiceMock.getLatestConversionRate(url = URL, base = BASE, symbols = SYMBOL) } returns mockedError

        val response = remoteDataSourceImpl.fetchConversionRate(url = URL, base = BASE, symbols = SYMBOL)

        assertFalse(response.leftOrNull() == null)
        assertEquals(response.leftOrNull()?.getFailureMessage(), info)
        assertTrue(response.isLeft)
    }

    @Test
    fun `given api service when response is successful return the object`() = runTest {

        val mockedSuccess = ConversionResultRaw(
            success = true,
            date = "2022-11-21",
            timestamp = 1669030923,
            base = BASE,
            rates = mutableMapOf<String, Double>().apply {
                put(SYMBOL, 1.845498)
            },
            errorBody = null
        )

        val expectedSuccess = true
        val expectedBase = BASE

        coEvery { apiServiceMock.getLatestConversionRate(url = URL, base = BASE, symbols = SYMBOL) } returns mockedSuccess

        val response = remoteDataSourceImpl.fetchConversionRate(url = URL, base = BASE, symbols = SYMBOL)

        assertEquals(response.rightOrNull()?.success, expectedSuccess)
        assertEquals(response.rightOrNull()?.base, expectedBase)
        assertTrue(response.rightOrNull()?.rates?.contains(SYMBOL) ?: false)
    }
}
