package com.middleton.restaurants.di

import android.app.Application
import android.content.Context
import com.middleton.restaurants.features.restaurant_search.data.remote.JustEatApi
import com.middleton.restaurants.features.restaurant_search.data.repository.RestaurantsRepositoryImpl
import com.middleton.restaurants.features.restaurant_search.domain.repository.RestaurantsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().build()
    }

    @Provides
    @Singleton
    fun provideJustEatApi(client: OkHttpClient): JustEatApi {
        return Retrofit.Builder()
            .baseUrl(JustEatApi.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(client)
            .build().create()
    }

    @Provides
    @Singleton
    fun provideRestaurantsRepository(
        api: JustEatApi
    ): RestaurantsRepository {
        return RestaurantsRepositoryImpl(
            api = api
        )
    }

    @Provides
    fun provideContext(application: Application): Context {
        return application
    }
}