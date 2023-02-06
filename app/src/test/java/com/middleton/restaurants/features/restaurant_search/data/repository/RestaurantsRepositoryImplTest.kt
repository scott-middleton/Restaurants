package com.middleton.restaurants.features.restaurant_search.data.repository

import com.middleton.restaurants.features.restaurant_search.data.remote.JustEatApi
import com.middleton.restaurants.features.restaurant_search.data.remote.emptyRestaurantsResponse
import com.middleton.restaurants.features.restaurant_search.domain.model.Restaurant
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.middleton.restaurants.features.restaurant_search.data.remote.validRestaurantsResponse
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalCoroutinesApi::class)
class RestaurantsRepositoryImplTest {
    private lateinit var repository: RestaurantsRepositoryImpl
    private lateinit var mockWebServer: MockWebServer
    private lateinit var okHttpClient: OkHttpClient
    private lateinit var api: JustEatApi

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        okHttpClient = OkHttpClient.Builder()
            .writeTimeout(1, TimeUnit.SECONDS)
            .readTimeout(1, TimeUnit.SECONDS)
            .connectTimeout(1, TimeUnit.SECONDS)
            .build()
        api = Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create())
            .client(okHttpClient)
            .baseUrl(mockWebServer.url("/"))
            .build()
            .create(JustEatApi::class.java)
        repository = RestaurantsRepositoryImpl(
            api = api
        )
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `Given API returns success response, when getRestaurantsByPostcode() called, valid response is emitted`() =
        runTest {
            mockWebServer.enqueue(
                MockResponse().setBody(validRestaurantsResponse)
            )

            val expected = listOf(
                Restaurant(
                    name = "Sopna Tandoori Restaurant",
                    rating = "5.3",
                    ratingCount = "281",
                    cuisineTypes = "Indian",
                    logoUrl = "http://d30v2pzvrfyzpo.cloudfront.net/uk/images/restaurants/157777.gif",
                    isOpenNow = true
                ), Restaurant(
                    name = "Bella Pizza",
                    rating = "4.0",
                    ratingCount = "84",
                    cuisineTypes = "Kebab - Pizza",
                    logoUrl = "http://d30v2pzvrfyzpo.cloudfront.net/uk/images/restaurants/189180.gif",
                    isOpenNow = true
                )
            )

            val result = repository.getRestaurantsByPostcode("LL551LL")

            result.onSuccess {
                assertEquals(expected, it)
            }
        }

    @Test
    fun `Given API returns error response, when getRestaurantsByPostcode() called, error is emitted`() = runTest {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(400)
        )

        val result = repository.getRestaurantsByPostcode("LL551LL")

        assertTrue(result.isFailure)
    }

    @Test
    fun `Given API returns response with no restaurants, when getRestaurantsByPostcode() called, empty list is emitted`() = runTest {
        mockWebServer.enqueue(
            MockResponse().setBody(emptyRestaurantsResponse)
        )

        val result = repository.getRestaurantsByPostcode("LL551LLL")

        result.onSuccess {
            assertTrue(it.isEmpty())
        }
    }
}