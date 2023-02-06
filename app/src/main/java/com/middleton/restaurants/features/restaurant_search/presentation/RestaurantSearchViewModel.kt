package com.middleton.restaurants.features.restaurant_search.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.middleton.restaurants.R
import com.middleton.restaurants.features.restaurant_search.domain.model.Restaurant
import com.middleton.restaurants.features.restaurant_search.domain.usecases.GetOpenRestaurants
import com.middleton.restaurants.features.restaurant_search.domain.usecases.DetectOutCode
import com.middleton.restaurants.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RestaurantSearchViewModel @Inject constructor(
    private val getOpenRestaurants: GetOpenRestaurants,
    private val detectOutCode: DetectOutCode
) : ViewModel() {

    private val _state = MutableStateFlow(RestaurantSearchState())
    val state: StateFlow<RestaurantSearchState>
        get() = _state

    private val uiActions = MutableSharedFlow<RestaurantSearchAction>()

    private val _restaurantSearchEvents = Channel<RestaurantSearchEvent>()
    val restaurantSearchEvents = _restaurantSearchEvents.receiveAsFlow()

    init {
        viewModelScope.launch {
            uiActions.collect {
                handleAction(it)
            }
        }
    }

    fun emitAction(action: RestaurantSearchAction) {
        viewModelScope.launch {
            uiActions.emit(action)
        }
    }

    private fun handleAction(action: RestaurantSearchAction) {
        when (action) {
            is RestaurantSearchAction.OnAutoDetectPostcode -> {
                viewModelScope.launch(Dispatchers.IO) {
                    detectOutCode(onSuccess = { outCode ->
                        searchRestaurants(outCode)
                        _state.value = state.value.copy(currentSearchValue = outCode)
                    }, onFailure = {
                        RestaurantSearchEvent.ShowSnackBarEvent(
                            UiText.StringResource(
                                R.string.search_error_message
                            )
                        )
                    })
                }
            }
            RestaurantSearchAction.OnPermissionsDenied -> {
                viewModelScope.launch {
                    _restaurantSearchEvents.send(
                        RestaurantSearchEvent.ShowSnackBarEvent(
                            UiText.StringResource(
                                R.string.permission_denied_message
                            )
                        )
                    )
                }
            }
            RestaurantSearchAction.OnShowPermissionRationale -> {
                viewModelScope.launch {
                    _restaurantSearchEvents.send(
                        RestaurantSearchEvent.ShowPermissionRationale(
                            UiText.StringResource(R.string.permission_rationale_message),
                            UiText.StringResource(R.string.permission_rationale_action_label)
                        )
                    )
                }
            }
            is RestaurantSearchAction.OnSearchByPostcode -> {
                searchRestaurants(action.postcode)
            }
            is RestaurantSearchAction.OnSearchValueUpdated -> {
                _state.value = state.value.copy(currentSearchValue = action.value)
            }
        }
    }

    private fun searchRestaurants(postcode: String) {
        if (postcode.isBlank()) {
            _state.value = _state.value.copy(restaurants = emptyList())
        } else {
            _state.value = _state.value.copy(isLoading = true)
            viewModelScope.launch {
                getOpenRestaurants(postcode).onSuccess { restaurants ->
                    _state.value =
                        _state.value.copy(restaurants = restaurants, isLoading = false)
                }.onFailure {
                    _state.value =
                        _state.value.copy(restaurants = emptyList(), isLoading = false)
                    _restaurantSearchEvents.send(
                        RestaurantSearchEvent.ShowSnackBarEvent(
                            UiText.StringResource(
                                R.string.search_error_message
                            )
                        )
                    )
                }
            }
        }
    }
}

data class RestaurantSearchState(
    val restaurants: List<Restaurant> = emptyList(),
    val isLoading: Boolean = false,
    val currentSearchValue: String = "",
    val hasShownLocationPermissionsOnce: Boolean = false
)

sealed class RestaurantSearchAction {
    data class OnAutoDetectPostcode(val context: Context) : RestaurantSearchAction()
    object OnShowPermissionRationale : RestaurantSearchAction()
    object OnPermissionsDenied : RestaurantSearchAction()

    data class OnSearchByPostcode(val postcode: String) : RestaurantSearchAction()

    data class OnSearchValueUpdated(val value: String) : RestaurantSearchAction()
}

sealed class RestaurantSearchEvent {
    data class ShowSnackBarEvent(val message: UiText) : RestaurantSearchEvent()
    data class ShowPermissionRationale(val message: UiText, val actionLabel: UiText) :
        RestaurantSearchEvent()
}
