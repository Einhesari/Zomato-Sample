package com.einhesari.zomatosample.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import com.einhesari.zomatosample.model.Restaurant
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class RestaurantsViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val findRestaurantRepository: FindRestaurantRepository
) :
    ViewModel() {

    private val userMovementThreshold = 1000f
    private val searchResult = BehaviorRelay.create<List<Restaurant>>()
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
            findRestaurantRepository.getNetworkErrors()
        )
    }

    fun needToMoveCamera(currentLocation: Location, lastLocation: Location?): Boolean {
        lastLocation?.let {
            return currentLocation.distanceTo(it) >= userMovementThreshold
        }
        return false
    }

    fun findNearRestaurant(location: Location) {
        findRestaurantRepository.findRestaurant(location)
    }

    fun getRestaurants() = findRestaurantRepository.getRestaurants()
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
        findRestaurantRepository.dispose()
    }
}