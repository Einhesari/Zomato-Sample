package com.einhesari.zomatosample.view

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.einhesari.zomatosample.R
import com.einhesari.zomatosample.databinding.FragmentRestaurantBinding
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
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


class RestaurantFragment : DaggerFragment(), PermissionsListener, OnMapReadyCallback {

    private lateinit var fragmentRestaurantBinding: FragmentRestaurantBinding
    private lateinit var fragmentView: View
    private lateinit var mapView: MapView
    private lateinit var map: MapboxMap
    private lateinit var permissionsManager: PermissionsManager
    private lateinit var currentLocation: Location
    private val mapZoom = 11.0

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
        if (PermissionsManager.areLocationPermissionsGranted(context)) {
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
            permissionsManager = PermissionsManager(this)
            permissionsManager.requestLocationPermissions(activity)
        }
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

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
        TODO("Not yet implemented")
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            showUserLocationOnMap()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        map = mapboxMap
        map.setStyle(Style.MAPBOX_STREETS) {
            showUserLocationOnMap()
        }

    }

}
