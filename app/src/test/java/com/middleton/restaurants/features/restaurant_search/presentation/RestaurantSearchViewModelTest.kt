package com.middleton.restaurants.features.restaurant_search.presentation

import app.cash.turbine.test
import com.middleton.restaurants.R
import com.middleton.restaurants.features.restaurant_search.data.repository.FakeRestaurantsRepository
import com.middleton.restaurants.features.restaurant_search.domain.usecases.DetectOutCode
import com.middleton.restaurants.features.restaurant_search.domain.usecases.GetOpenRestaurants
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
    fun `When OnPermissionsDenied action emitted, then snackBar event is emitted`() = runTest {
        sut.emitAction(RestaurantSearchAction.OnPermissionsDenied)

        sut.restaurantSearchEvents.test {
            val item = awaitItem()
            assertEquals(
                RestaurantSearchEvent.ShowSnackBarEvent(
                    UiText.StringResource(R.string.permission_denied_message)
                ), item
            )
        }
    }

    @Test
    fun `When OnSearchValueUpdated action emitted, then state is emitted with new value`() =
        runTest {
            sut.emitAction(RestaurantSearchAction.OnSearchValueUpdated("CF14"))

            sut.state.test {
                val item = awaitItem()
                assertEquals(RestaurantSearchState(currentSearchValue = "CF14"), item)
            }
        }

    @Test
    fun `When OnShowPermissionRationale action emitted, then ShowPermissionRationale event is emitted`() = runTest {
        sut.emitAction(RestaurantSearchAction.OnShowPermissionRationale)

        sut.restaurantSearchEvents.test {
            val item = awaitItem()
            assertEquals(
                RestaurantSearchEvent.ShowPermissionRationale(
                    UiText.StringResource(R.string.permission_rationale_message),
                    UiText.StringResource(R.string.permission_rationale_action_label)
                ), item
            )
        }
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

            sut.restaurantSearchEvents.test {
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