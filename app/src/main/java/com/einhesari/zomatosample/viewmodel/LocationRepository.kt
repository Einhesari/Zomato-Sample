package com.einhesari.zomatosample.viewmodel

import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.jakewharton.rxrelay2.BehaviorRelay
import java.lang.Error
import java.lang.Exception
import javax.inject.Inject


class LocationRepository @Inject constructor(
    private val fusedLocationClient: FusedLocationProviderClient,
    private val context: Context
) {
    private val locationErrors: BehaviorRelay<Exception> = BehaviorRelay.create()
    private val liveLocation: BehaviorRelay<Location> = BehaviorRelay.create()

    private lateinit var locationCallback: LocationCallback

    private val requestPriority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
    private var locationRequest: LocationRequest? = null

    private val locationRequestInterval = 10000L
    private val fastestRequestInterval = 5000L

    fun checkLocationServiceAndStartLocationUpdate() {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest!!)
        val client: SettingsClient = LocationServices.getSettingsClient(context)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener { locationSettingsResponse ->
            startLocationUpdates()
        }
        task.addOnFailureListener {
            locationErrors.accept(it)
        }
    }

    fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    fun setupLocationChangeCallBack() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    liveLocation.accept(location)
                }
            }
        }
    }

    fun createLocationRequest() {
        locationRequest = LocationRequest.create()?.apply {
            interval = locationRequestInterval
            fastestInterval = fastestRequestInterval
            priority = requestPriority
        }
    }

    fun getUserLiveLocation() = liveLocation.hide()
    fun getlocationErrors() = locationErrors.hide()
}
