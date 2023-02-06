package com.middleton.restaurants.features.restaurant_search.presentation

import app.cash.turbine.test
import com.middleton.restaurants.R
import com.middleton.restaurants.features.restaurant_search.data.repository.FakeRestaurantsRepository
import com.middleton.restaurants.features.restaurant_search.domain.usecases.GetOpenRestaurants
import com.middleton.restaurants.features.restaurant_search.domain.usecases.DetectOutCode
import com.middleton.restaurants.util.UiText
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RestaurantSearchViewModelTest {
    private var repository = FakeRestaurantsRepository()
    private lateinit var getOpenRestaurants: GetOpenRestaurants
    private lateinit var detectOutCode: DetectOutCode

    private lateinit var sut: RestaurantSearchViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Before
    fun setUp() {
        getOpenRestaurants = GetOpenRestaurants(repository)
        detectOutCode = mockk()

        sut = RestaurantSearchViewModel(getOpenRestaurants, detectOutCode)
    }

    @Test
    fun `Given repo returns error, when searchByPostcode, then state contains empty list and snackbar event emitted`() =
        runTest {
            repository.shouldReturnError = true

            sut.emitAction(RestaurantSearchAction.OnSearchByPostcode("CF143NN"))

            val expectedState = RestaurantSearchState(restaurants = emptyList(), isLoading = false)

            sut.state.test {
                val item = awaitItem()
                assertEquals(expectedState, item)
            }

            sut.restaurantSearchEvent.test {
                assertEquals(
                    RestaurantSearchEvent.ShowSnackBarEvent(
                        UiText.StringResource(R.string.search_error_message)
                    ),
                    awaitItem()
                )
            }
        }

    @Test
    fun `Given repo returns success, when searchByPostcode, then expected state is emitted`() =
        runTest {
            repository.shouldReturnError = false

            sut.emitAction(RestaurantSearchAction.OnSearchByPostcode("CF143NN"))

            val expectedState =
                RestaurantSearchState(
                    restaurants = getOpenRestaurants.invoke("CF143NN").getOrThrow(),
                    isLoading = false
                )

            sut.state.test {
                val item = awaitItem()
                assertEquals(expectedState, item)
            }
        }

    @Test
    fun `Given empty string passed as postcode, when searchByPostcode, then expected empty list state is emitted`() =
        runTest {
            repository.shouldReturnError = false

            sut.emitAction(RestaurantSearchAction.OnSearchByPostcode(""))

            val expectedState = RestaurantSearchState(restaurants = emptyList())

            sut.state.test {
                val item = awaitItem()
                assertEquals(expectedState, item)
            }
        }
}