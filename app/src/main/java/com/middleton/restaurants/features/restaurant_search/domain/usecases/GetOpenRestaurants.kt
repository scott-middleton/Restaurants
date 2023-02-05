package com.middleton.restaurants.features.restaurant_search.domain.usecases

import com.middleton.restaurants.features.restaurant_search.domain.model.Restaurant
import com.middleton.restaurants.features.restaurant_search.domain.repository.RestaurantsRepository
import javax.inject.Inject

class GetOpenRestaurants @Inject constructor(private val repository: RestaurantsRepository) {
    suspend operator fun invoke(postcode: String): Result<List<Restaurant>> {
        return repository.getRestaurantsByPostcode(postcode).map { restaurants ->
            restaurants.filter { it.isOpenNow }
        }
    }
}
