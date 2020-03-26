package com.einhesari.zomatosample.viewmodel

import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import com.einhesari.zomatosample.model.Restaurant
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class RestaurantsViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val searchRestaurantRepository: SearchRestaurantRepository
) :
    ViewModel() {

    private val userMovementThreshold = 1000f
    private val searchResult = BehaviorRelay.create<List<Restaurant>>()
    private val searchErrors: BehaviorRelay<Throwable> = BehaviorRelay.create()
    private val compositeDisposable = CompositeDisposable()

    fun initUserLocation() {
        locationRepository.createLocationRequest()
        locationRepository.setupLocationChangeCallBack()
        locationRepository.checkLocationServiceAndStartLocationUpdate()
    }

    fun getUserLiveLocation() = locationRepository.getUserLiveLocation()
    fun errors(): Observable<Throwable> {
        return Observable.merge(
            locationRepository.getlocationErrors(),
            searchRestaurantRepository.getNetworkErrors()
        )
    }

    fun needToMoveCamera(currentLocation: Location, lastLocation: Location?): Boolean {
        lastLocation?.let {
            return currentLocation.distanceTo(it) >= userMovementThreshold
        }
        return false
    }

    fun findNearRestaurant(location: Location) {
        searchRestaurantRepository.findRestaurant(location)
    }

    fun getRestaurants() = searchRestaurantRepository.getRestaurants()
    fun getSearchResult() = searchResult.hide()


    fun searchRestaurant(query: String, restaurants: ArrayList<Restaurant>) {
        val queryWithoutSpace = query.replace("\\s".toRegex(), "")
        val result = ArrayList<Restaurant>()
        restaurants.forEach {
            val restaurantNameWithoutSpace = it.name.replace("\\s".toRegex(), "")
            if (restaurantNameWithoutSpace.contains(queryWithoutSpace, true)) {
                result.add(it)
            }
        }
        searchResult.accept(result)
    }

    override fun onCleared() {
        super.onCleared()
        searchRestaurantRepository.dispose()
        compositeDisposable.dispose()
    }
}