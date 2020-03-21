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
    private var foundedRestaurants = ArrayList<Restaurant>()

    fun findRestaurant(location: Location) {
        apiService.findRestaurant(
                location.latitude.toString(),
                location.longitude.toString(),
                searchRadius
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                it.restaurants.all {
                    foundedRestaurants.add(it.restaurant)
                }
                restaurants.accept(foundedRestaurants)
            }, {
                it.printStackTrace()
            })
            .let { }
    }

    fun getRestaurants() = restaurants.hide()
}