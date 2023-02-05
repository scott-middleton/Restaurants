package com.middleton.restaurants.features.restaurant_search.data.repository

import com.middleton.restaurants.features.restaurant_search.data.remote.JustEatApi
import com.middleton.restaurants.features.restaurant_search.domain.mappers.toRestaurantList
import com.middleton.restaurants.features.restaurant_search.domain.model.Restaurant
import com.middleton.restaurants.features.restaurant_search.domain.repository.RestaurantsRepository
import javax.inject.Inject

class RestaurantsRepositoryImpl @Inject constructor(private val api: JustEatApi) :
    RestaurantsRepository {

    override suspend fun getRestaurantsByPostcode(postCode: String): Result<List<Restaurant>> {
        return try {
            val response = api.getRestaurantsByPostcode(postCode.filter { !it.isWhitespace() })
            if (response.isSuccessful && response.body()?.Restaurants != null) {
                Result.success(response.body()!!.toRestaurantList())
            } else {
                Result.failure(Exception("Here is my exception!!"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}