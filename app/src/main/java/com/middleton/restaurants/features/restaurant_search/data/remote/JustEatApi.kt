package com.middleton.restaurants.features.restaurant_search.data.remote

import com.middleton.restaurants.features.restaurant_search.data.remote.dto.RestaurantListDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface JustEatApi {

    @GET("restaurants/bypostcode/{postcode}")
    suspend fun getRestaurantsByPostcode(@Path("postcode") postcode: String) : Response<RestaurantListDto>

    companion object {
        const val BASE_URL = "https://uk.api.just-eat.io/"
    }
}