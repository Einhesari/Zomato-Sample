package com.einhesari.zomatosample.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import com.einhesari.zomatosample.model.Restaurant
import com.einhesari.zomatosample.utils.Const.SEARCH_RADIUS
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class RestaurantsViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val remoteApiRepository: RemoteApiRepository
) :
    ViewModel() {
    private val state: BehaviorRelay<RestaurantFragmentState> = BehaviorRelay.create()
    private val compositeDisposable = CompositeDisposable()
    private var allRestaurant = ArrayList<Restaurant>()
    private var inRangeRestaurant = ArrayList<Restaurant>()

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
        remoteApiRepository.findRestaurant(
            location
        )
            .subscribe({
                it.restaurants.forEach {
                    allRestaurant.add(it.restaurant)
                }
                inRangeRestaurant = inRangeRestaurants(location, allRestaurant)
                if (inRangeRestaurant.size > 0) {
                    state.accept(
                        RestaurantFragmentState.FetchedRestaurantsSuccessfully(
                            inRangeRestaurant
                        )
                    )
                } else {
                    state.accept(
                        RestaurantFragmentState.NoNearRestuarants
                    )
                }
            }, {
                state.accept(RestaurantFragmentState.Error(it))
            }).let {
                compositeDisposable.add(it)
            }
    }

    fun searchRestaurant(query: String) {
        if (query.isBlank()) {
            state.accept(RestaurantFragmentState.FetchedRestaurantsSuccessfully(inRangeRestaurant))
            return
        }
        val queryWithoutSpace = query.replace("\\s".toRegex(), "")
        val result = ArrayList<Restaurant>()
        inRangeRestaurant.forEach {
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
            state.accept(RestaurantFragmentState.FetchedRestaurantsSuccessfully(inRangeRestaurant))
        }
    }

    private fun inRangeRestaurants(
        location: Location,
        allRestaurants: ArrayList<Restaurant>
    ): ArrayList<Restaurant> {
        val nearRestaurants = ArrayList<Restaurant>()
        allRestaurants.forEach {
            val restaurantLocation = Location("Restaurant Location")
            restaurantLocation.apply {
                latitude = it.restaurantLocation.latitude.toDouble()
                longitude = it.restaurantLocation.longitude.toDouble()
            }
            if (location.distanceTo(restaurantLocation) < SEARCH_RADIUS) {
                nearRestaurants.add(it)
            }
        }
        return nearRestaurants
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
    data class FetchedRestaurantsSuccessfully(val restaurants: ArrayList<Restaurant>) :
        RestaurantFragmentState()

    object NoNearRestuarants : RestaurantFragmentState()
    data class SearchedRestaurants(val restaurants: ArrayList<Restaurant>) :
        RestaurantFragmentState()

    data class Error(val error: Throwable) : RestaurantFragmentState()
}