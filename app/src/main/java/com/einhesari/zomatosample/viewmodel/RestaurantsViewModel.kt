package com.einhesari.zomatosample.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import com.einhesari.zomatosample.model.Restaurant
import com.einhesari.zomatosample.network.ApiService
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.lang.reflect.Array
import javax.inject.Inject

class RestaurantsViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val apiService: ApiService
) :
    ViewModel() {
    private val state: BehaviorRelay<RestaurantFragmentState> = BehaviorRelay.create()
    private val compositeDisposable = CompositeDisposable()
    private var allRestaurant = ArrayList<Restaurant>()
    private val searchRadius = "1000" // in meters


    fun getState() = state.hide()
    fun setState(state: RestaurantFragmentState) {
        this.state.accept(state)
    }

    fun initUserLocation() {
        state.accept(RestaurantFragmentState.Loading)
        locationRepository.getUserLiveLocation()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                state.accept(RestaurantFragmentState.GotUserLocationSuccessfully(it))
            }, {
                state.accept(RestaurantFragmentState.Error(it))
            }).let {
                compositeDisposable.add(it)
            }
        locationRepository.checkLocationServiceAndStartLocationUpdate()
    }


    fun findNearRestaurant(location: Location) {
        state.accept(RestaurantFragmentState.Loading)
        apiService.findRestaurant(
                location.latitude.toString(),
                location.longitude.toString(),
                searchRadius
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                it.restaurants.forEach {
                    allRestaurant.add(it.restaurant)
                }
                state.accept(RestaurantFragmentState.FetchedRestaurantsSuccessfully(allRestaurant))
            }, {
                state.accept(RestaurantFragmentState.Error(it))
            }).let {
                compositeDisposable.add(it)
            }
    }

    fun searchRestaurant(query: String, restaurants: List<Restaurant>) {
        if (query.isBlank()) {
            state.accept(RestaurantFragmentState.FetchedRestaurantsSuccessfully(allRestaurant))
            return
        }
        val queryWithoutSpace = query.replace("\\s".toRegex(), "")
        val result = ArrayList<Restaurant>()
        restaurants.forEach {
            val restaurantNameWithoutSpace = it.name.replace("\\s".toRegex(), "")
            val restaurantCuisineWithoutSpace = it.cuisines.replace("\\s".toRegex(), "")
            if (restaurantNameWithoutSpace.contains(queryWithoutSpace, true)) {
                result.add(it)
            } else if (restaurantCuisineWithoutSpace.contains(queryWithoutSpace, true)) {
                result.add(it)
            }
        }
        if (result.size > 0) {
            state.accept(RestaurantFragmentState.SearchedRestaurants(result))
        } else {
            state.accept(RestaurantFragmentState.FetchedRestaurantsSuccessfully(allRestaurant))
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

}

sealed class RestaurantFragmentState {
    object NeedPermission : RestaurantFragmentState()
    object PermissionDenied : RestaurantFragmentState()
    object PermissionGranted : RestaurantFragmentState()
    object ChangeLocationSettingsDenied : RestaurantFragmentState()
    object ChangeLocationSettingsAllowed : RestaurantFragmentState()
    object Loading : RestaurantFragmentState()
    data class GotUserLocationSuccessfully(val location: Location) : RestaurantFragmentState()
    data class FetchedRestaurantsSuccessfully(val restaurants: List<Restaurant>) :
        RestaurantFragmentState()

    data class SearchedRestaurants(val restaurants: List<Restaurant>) : RestaurantFragmentState()
    data class Error(val error: Throwable) : RestaurantFragmentState()
}