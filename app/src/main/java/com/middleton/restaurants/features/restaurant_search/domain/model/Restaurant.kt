package com.middleton.restaurants.features.restaurant_search.domain.model

data class Restaurant(
    val name: String,
    val rating: String,
    val ratingCount: String,
    val cuisineTypes: String,
    val logoUrl: String,
    val isOpenNow: Boolean
)
