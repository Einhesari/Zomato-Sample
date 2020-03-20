package com.einhesari.zomatosample.view

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.einhesari.zomatosample.R
import com.einhesari.zomatosample.databinding.FragmentRestaurantBinding
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import dagger.android.support.DaggerFragment


class RestaurantFragment : DaggerFragment(), OnMapReadyCallback {

    private lateinit var fragmentRestaurantBinding: FragmentRestaurantBinding
    private lateinit var fragmentView: View
    private lateinit var mapView: MapView
    private lateinit var map: MapboxMap
    private lateinit var currentLocation: Location
    private val mapZoom = 13.0
    private lateinit var locationCallback: LocationCallback
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val locationRequestInterval = 10000L
    private val fastestRequestInterval = 5000L
    private val requestPriority = LocationRequest.PRIORITY_HIGH_ACCURACY
    private var locationRequest: LocationRequest? = null

    var permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentRestaurantBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_restaurant, container, false)
        fragmentView = fragmentRestaurantBinding.root
        return fragmentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView = fragmentRestaurantBinding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        setupLocationChangeCallBack()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
    }


    private fun showUserLocationOnMap() {
        val customLocationComponentOptions =
            createLocationComponentOptions()
        val customLocationComponentActivationOptions =
            createLocationComponentActivationOptions(customLocationComponentOptions)
        map.locationComponent.apply {

            activateLocationComponent(customLocationComponentActivationOptions)

            isLocationComponentEnabled = true

            cameraMode = CameraMode.TRACKING

            renderMode = RenderMode.COMPASS
        }

    }

    fun hasPermissions(permissions: Array<String>): Boolean = permissions.all {
        ActivityCompat.checkSelfPermission(context!!, it) == PackageManager.PERMISSION_GRANTED
    }


    private fun moveCameraLocation(location: Location) {
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    location.latitude,
                    location.longitude
                ), mapZoom
            )
        )
    }

    private fun createLocationComponentOptions() =
        LocationComponentOptions.builder(context!!)
            .trackingGesturesManagement(true)
            .accuracyColor(ContextCompat.getColor(context!!, R.color.colorAccent))
            .build()

    private fun createLocationComponentActivationOptions(locationComponentOptions: LocationComponentOptions) =
        LocationComponentActivationOptions.builder(context!!, map.style!!)
            .locationComponentOptions(locationComponentOptions)
            .build()


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.all {
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            }) {
            showUserLocationOnMap()
        }

    }


    override fun onMapReady(mapboxMap: MapboxMap) {
        map = mapboxMap
        map.setStyle(Style.MAPBOX_STREETS) {
            if (hasPermissions(permissions)) {
                showUserLocationOnMap()
                createLocationRequest()
                checkLocationService()
            } else {
                requestPermissions(
                    permissions, 1
                )
            }
        }



    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest.create()?.apply {
            interval = locationRequestInterval
            fastestInterval = fastestRequestInterval
            priority = requestPriority
        }
    }

    private fun checkLocationService() {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest!!)
        val client: SettingsClient = LocationServices.getSettingsClient(activity!!)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener { locationSettingsResponse ->
            startLocationUpdates()
        }
        task.addOnFailureListener {

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
                    Log.i("", "")
                    moveCameraLocation(location)
//                    showUserLocationOnMap()
                }
            }
        }
    }
}
