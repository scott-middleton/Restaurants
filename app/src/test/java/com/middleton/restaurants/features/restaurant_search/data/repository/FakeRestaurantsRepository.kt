package com.middleton.restaurants.features.restaurant_search.data.repository

import com.middleton.restaurants.features.restaurant_search.domain.model.Restaurant
import com.middleton.restaurants.features.restaurant_search.domain.repository.RestaurantsRepository

class FakeRestaurantsRepository : RestaurantsRepository {
    var shouldReturnError = false
    var shouldReturnEmptyList = false

    private val restaurants = provideRestaurants()

    override suspend fun getRestaurantsByPostcode(postCode: String): Result<List<Restaurant>> {
        return if (shouldReturnError) {
            Result.failure(Throwable())
        } else {
            if (shouldReturnEmptyList) {
                Result.success(emptyList())
            } else {
                Result.success(restaurants)
            }
        }
    }

    private fun provideRestaurants(): MutableList<Restaurant> {
        return mutableListOf(
            Restaurant(
                name = "Sample Restaurant 1",
                rating = "4.4",
                ratingCount = "137",
                cuisineTypes = "",
                logoUrl = "http://sample-restaurant-1.com/logo.jpg",
                isOpenNow = true
            ), Restaurant(
                name = "Sample Restaurant 2",
                rating = "3.5",
                ratingCount = "50",
                cuisineTypes = "Mexican",
                logoUrl = "http://sample-restaurant-2.com/logo.jpg",
                isOpenNow = false
            ), Restaurant(
                name = "Sample Restaurant 3",
                rating = "4.2",
                ratingCount = "100",
                cuisineTypes = "Chinese",
                logoUrl = "http://sample-restaurant-3.com/logo.jpg",
                isOpenNow = true
            ), Restaurant(
                name = "Sample Restaurant 4",
                rating = "4.0",
                ratingCount = "75",
                cuisineTypes = "Indian",
                logoUrl = "http://sample-restaurant-4.com/logo.jpg",
                isOpenNow = false
            ), Restaurant(
                name = "Sample Restaurant 5",
                rating = "3.8",
                ratingCount = "60",
                cuisineTypes = "Thai",
                logoUrl = "http://sample-restaurant-5.com/logo.jpg",
                isOpenNow = true
            )
        )
    }
}