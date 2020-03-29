package com.einhesari.zomatosample.viewmodel

import android.content.Context
import android.location.Location
import android.os.Looper
import com.einhesari.zomatosample.model.Restaurant
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.internal.operators.single.SingleObserveOn
import io.reactivex.subjects.PublishSubject
import java.lang.Error
import java.lang.Exception
import javax.inject.Inject


class LocationRepository @Inject constructor(
    private val fusedLocationClient: FusedLocationProviderClient,
    private val locationRequest: LocationRequest,
    private val context: Context
) {

    private lateinit var liveLocation: PublishSubject<Location>
    private lateinit var locationCallback: LocationCallback

    init {
        setupLocationChangeCallBack()
    }

    fun checkLocationServiceAndStartLocationUpdate() {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(context)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener { locationSettingsResponse ->
            startLocationUpdates()
        }
        task.addOnFailureListener {
            liveLocation.onError(it)
        }
    }

    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun setupLocationChangeCallBack() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    liveLocation.onNext(location)
                }
                fusedLocationClient.removeLocationUpdates(locationCallback)
                liveLocation.onComplete()
            }
        }
    }

    fun getUserLiveLocation(): Observable<Location> {
        liveLocation = PublishSubject.create()
        return liveLocation.hide()
    }
}
