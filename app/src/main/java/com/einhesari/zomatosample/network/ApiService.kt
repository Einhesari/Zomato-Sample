package com.einhesari.zomatosample.network

import com.einhesari.zomatosample.model.RestaurantSearchResponse
import io.reactivex.Single
import retrofit2.http.GET

interface ApiService {
    @GET("/search")
    fun findRestaurant(lat: String, long: String, radius: String): Single<RestaurantSearchResponse>

}