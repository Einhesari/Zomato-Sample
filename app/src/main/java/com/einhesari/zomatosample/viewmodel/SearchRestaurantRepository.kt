package com.einhesari.zomatosample.viewmodel

import android.location.Location
import com.einhesari.zomatosample.model.Restaurant
import com.einhesari.zomatosample.network.ApiService
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class SearchRestaurantRepository @Inject constructor(private val apiService: ApiService) {

    private val searchRadius = "1000" // in meters
    private val restaurants: BehaviorRelay<ArrayList<Restaurant>> = BehaviorRelay.create()


    fun findRestaurant(location: Location) {
        apiService.findRestaurant(
                location.latitude.toString(),
                location.longitude.toString(),
                searchRadius
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                restaurants.accept(it.Restaurants)
            }, { error ->
                error.printStackTrace()
            })
            .let { }
    }

    fun getRestaurants() = restaurants.hide()
}