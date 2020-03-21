package com.einhesari.zomatosample.network

import com.einhesari.zomatosample.model.RestaurantSearchResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("/search")
    fun findRestaurant(
        @Query("lat") lat: String,
        @Query("lon") long: String,
        @Query("radius") radius: String
    ): Single<RestaurantSearchResponse>

}