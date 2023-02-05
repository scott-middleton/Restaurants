package com.middleton.restaurants.features.restaurant_search.domain.usecases

import com.middleton.restaurants.features.restaurant_search.data.repository.FakeRestaurantsRepository
import com.middleton.restaurants.features.restaurant_search.domain.model.Restaurant
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetOpenRestaurantsTest {
    private lateinit var repository: FakeRestaurantsRepository
    private lateinit var sut: GetOpenRestaurants

    @Before
    fun setup() {
        repository = FakeRestaurantsRepository()
        sut = GetOpenRestaurants(repository)
    }

    @Test
    fun `Given repo returns success, when UseCase is invoked, then sut returns open restaurants`() =
        runTest {
            val actual = sut.invoke("CF143NN")

            actual.getOrNull()?.forEach { restaurant ->
                assertTrue(restaurant.isOpenNow)
            }
        }

    @Test
    fun `Given repo returns success with empty list, when UseCase is invoked, then sut returns empty list`() =
        runTest {
            repository.shouldReturnEmptyList = true

            val actual = sut.invoke("")

            val expected = Result.success(emptyList<Restaurant>())

            assertEquals(expected, actual)
        }

    @Test
    fun `Given repo returns failure, when UseCase is invoked, then sut returns failure`() =
        runTest {
            repository.shouldReturnError = true

            val result = sut("CF143NN")

            assertTrue(result.isFailure)
        }

}