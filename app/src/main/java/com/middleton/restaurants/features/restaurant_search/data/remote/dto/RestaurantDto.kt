package com.middleton.restaurants.features.restaurant_search.data.remote.dto

data class RestaurantListDto (
    val Restaurants: List<RestaurantDto>
)

data class RestaurantDto(
    val Name: String,
    val Cuisines: List<CuisineDto>,
    val LogoUrl: String,
    val Rating: RatingDto,
    val IsOpenNow: Boolean
)

data class CuisineDto(
    val Name: String
)

data class RatingDto(
    val Average: Double,
    val Count: Int,
    val StarRating: Double
)