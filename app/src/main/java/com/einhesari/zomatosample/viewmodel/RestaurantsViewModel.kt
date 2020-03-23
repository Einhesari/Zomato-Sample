package com.einhesari.zomatosample.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import javax.inject.Inject

class RestaurantsViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val searchRestaurantRepository: SearchRestaurantRepository
) :
    ViewModel() {

    private val userMovementThreshold = 1000f

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

    override fun onCleared() {
        super.onCleared()
        searchRestaurantRepository.dispose()
    }
}