package com.einhesari.zomatosample.di.module

import android.app.Application
import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class LocationModule {

    private val locationRequestInterval = 10000L
    private val fastestRequestInterval = 5000L
    private val requestPriority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY

    @Provides
    @Singleton
    fun providesFusedLocationClientModule(context: Context): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }

    @Provides
    @Singleton
    fun provideLocationRequest(): LocationRequest {
        return LocationRequest.create().apply {
            interval = locationRequestInterval
            fastestInterval = fastestRequestInterval
            priority = requestPriority
        }
    }
}