package com.einhesari.zomatosample.viewmodel

import android.location.Location
import com.einhesari.zomatosample.model.Restaurant
import com.einhesari.zomatosample.network.ApiService
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class SearchRestaurantRepository @Inject constructor(private val apiService: ApiService) {

    private val searchRadius = "1000" // in meters
    private val restaurants: BehaviorRelay<ArrayList<Restaurant>> = BehaviorRelay.create()
    private val errors: BehaviorRelay<Throwable> = BehaviorRelay.create()
    private var foundedRestaurants = ArrayList<Restaurant>()
    private val compositeDisposable = CompositeDisposable()

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
                errors.accept(it)
            })
            .let { compositeDisposable.add(it) }
    }

    fun getRestaurants(): Observable<ArrayList<Restaurant>> {
        return restaurants.hide()
    }

    fun getNetworkErrors() = errors.hide()

    fun dispose() {
        compositeDisposable.dispose()
    }
}