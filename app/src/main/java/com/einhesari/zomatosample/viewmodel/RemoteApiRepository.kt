package com.einhesari.zomatosample.viewmodel

import android.location.Location
import com.einhesari.zomatosample.model.RestaurantSearchResponse
import com.einhesari.zomatosample.network.ApiService
import com.einhesari.zomatosample.utils.Const.RESTAURATNS_RADIUS
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class RemoteApiRepository @Inject constructor(private val apiService: ApiService) {


    fun findRestaurant(location: Location): Single<RestaurantSearchResponse> {

        return apiService.findRestaurant(
            location.latitude.toString(),
            location.longitude.toString(),
            RESTAURATNS_RADIUS.toString()
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    }


}