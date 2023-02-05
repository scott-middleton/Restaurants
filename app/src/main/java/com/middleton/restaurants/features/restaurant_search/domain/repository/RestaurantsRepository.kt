package com.middleton.restaurants.features.restaurant_search.domain.repository

import com.middleton.restaurants.features.restaurant_search.domain.model.Restaurant

interface RestaurantsRepository {

    suspend fun getRestaurantsByPostcode(postCode: String): Result<List<Restaurant>>
}