package com.einhesari.zomatosample.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.einhesari.zomatosample.R
import com.einhesari.zomatosample.databinding.FragmentRestaurantBinding
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
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
    private val mapZoom = 11.0

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
    }


    private fun showUserLocationOnMap() {
        if (hasPermissions(permissions)) {
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
            getLastKnownLocation()
            moveCameraLocation(currentLocation)
        } else {
            requestPermissions(
                permissions, 1
            )
        }
    }

    fun hasPermissions(permissions: Array<String>): Boolean = permissions.all {
        ActivityCompat.checkSelfPermission(context!!, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun getLastKnownLocation() {
        currentLocation = map.locationComponent.lastKnownLocation!!
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
            showUserLocationOnMap()
        }

    }

}
