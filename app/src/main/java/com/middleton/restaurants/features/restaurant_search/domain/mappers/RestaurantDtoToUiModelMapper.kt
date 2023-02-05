package com.middleton.restaurants.features.restaurant_search.domain.mappers

import com.middleton.restaurants.features.restaurant_search.data.remote.dto.RestaurantDto
import com.middleton.restaurants.features.restaurant_search.data.remote.dto.RestaurantListDto
import com.middleton.restaurants.features.restaurant_search.domain.model.Restaurant


fun RestaurantListDto.toRestaurantList(): List<Restaurant> {
    return this.Restaurants.map { it.toRestaurant() }
}

fun RestaurantDto.toRestaurant(): Restaurant {
    return Restaurant(
        name = this.Name,
        rating = this.Rating.Average.toString(),
        ratingCount = this.Rating.Count.toString(),
        cuisineTypes = this.Cuisines.joinToString(" - ") { it.Name },
        logoUrl = this.LogoUrl,
        isOpenNow = this.IsOpenNow
    )
}