package com.middleton.restaurants.features.restaurant_search.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.middleton.restaurants.R
import com.middleton.restaurants.features.restaurant_search.domain.model.Restaurant
import com.middleton.restaurants.features.restaurant_search.domain.usecases.GetOpenRestaurants
import com.middleton.restaurants.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RestaurantSearchViewModel @Inject constructor(private val getOpenRestaurants: GetOpenRestaurants) :
    ViewModel() {

    private val _state = MutableStateFlow(RestaurantsState())
    val state: StateFlow<RestaurantsState>
        get() = _state

    private val _snackBarEvent = Channel<RestaurantsSnackBarEvent>()
    val snackBarEvent = _snackBarEvent.receiveAsFlow()

    fun searchByPostcode(postCode: String) {
        if (postCode.isBlank()) {
            _state.value = _state.value.copy(restaurants = emptyList())
        } else {
            _state.value = _state.value.copy(isLoading = true)
            viewModelScope.launch {
                getOpenRestaurants(postCode).onSuccess { restaurants ->
                    _state.value = _state.value.copy(restaurants = restaurants, isLoading = false)
                }.onFailure {
                    _state.value = _state.value.copy(restaurants = emptyList(), isLoading = false)
                    _snackBarEvent.send(RestaurantsSnackBarEvent(UiText.StringResource(R.string.search_error_message)))
                }
            }
        }
    }
}

data class RestaurantsState(
    val restaurants: List<Restaurant> = emptyList(),
    val isLoading: Boolean = false
)

data class RestaurantsSnackBarEvent(val message: UiText)