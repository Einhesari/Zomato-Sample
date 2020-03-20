package com.einhesari.zomatosample.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class RestaurantsViewModel @Inject constructor(private val repository: LocationRepository) :
    ViewModel() {

    private val userMovementThreshold = 1000f

    fun initUserLocation() {
        repository.createLocationRequest()
        repository.setupLocationChangeCallBack()
        repository.checkLocationServiceAndStartLocationUpdate()
    }

    fun getUserLiveLocation() = repository.getUserLiveLocation()

    fun needToMoveCamera(currentLocation: Location, lastLocation: Location?): Boolean {
        lastLocation?.let {
            return currentLocation.distanceTo(it) >= userMovementThreshold
        }
        return false
    }
}