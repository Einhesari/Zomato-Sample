package com.einhesari.zomatosample.view

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.einhesari.zomatosample.R
import com.einhesari.zomatosample.databinding.FragmentRestaurantBinding
import com.einhesari.zomatosample.viewmodel.RestaurantsViewModel
import com.einhesari.zomatosample.viewmodel.ViewModelProviderFactory
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.*
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import com.mapbox.mapboxsdk.style.layers.Property.ICON_ROTATION_ALIGNMENT_VIEWPORT
import com.mapbox.mapboxsdk.utils.BitmapUtils
import dagger.android.support.DaggerFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import kotlin.math.log


class RestaurantFragment : DaggerFragment(), OnMapReadyCallback {

    private lateinit var fragmentRestaurantBinding: FragmentRestaurantBinding
    private lateinit var compositeDisposable: CompositeDisposable

    private lateinit var mapView: MapView
    private lateinit var map: MapboxMap
    private lateinit var symbolManager: SymbolManager

    private val RESTAURANT_MARKER_ID = "restaurant"

    private var lastLocation: Location? = null

    private val REQUEST_CODE = 1000
    private val mapZoom = 13.0

    @Inject
    lateinit var factory: ViewModelProviderFactory

    private lateinit var viewmodel: RestaurantsViewModel

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
        return fragmentRestaurantBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        compositeDisposable = CompositeDisposable()

        viewmodel = ViewModelProvider(this, factory)[RestaurantsViewModel::class.java]

        mapView = fragmentRestaurantBinding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        getNearRestuarants()
    }

    private fun getNearRestuarants() {
        viewmodel.getRestaurants()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                it.forEach {
                    val latLng =
                        LatLng(
                            it.restaurantLocation.latitude.toDouble(),
                            it.restaurantLocation.longitude.toDouble()
                        )
                    addMarkerOnMap(latLng)

                }
            }
            .let {
                compositeDisposable.add(it)
            }
    }

    private fun observeUserLiveLocation() {
        viewmodel.getUserLiveLocation()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                lastLocation?.also { lastLocation ->
                    if (viewmodel.needToMoveCamera(it, lastLocation)) {
                        moveCameraLocation(it)
                    }
                    this.lastLocation = it
                } ?: run {
                    lastLocation = it
                    moveCameraLocation(it)
                    viewmodel.findNearRestaurant(it)
                }

            }
            .let {
                compositeDisposable.add(it)
            }
    }

    private fun showUserMarkerOnMap() {
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
                it == PackageManager.PERMISSION_GRANTED
            }) {
            showUserLocationOnMap()
        }

    }


    override fun onMapReady(mapboxMap: MapboxMap) {
        map = mapboxMap
        map.setStyle(Style.MAPBOX_STREETS) {

            it.addImage(
                RESTAURANT_MARKER_ID,
                BitmapFactory.decodeResource(
                    context?.getResources(),
                    R.drawable.restaurant_marker
                ),
                false
            )

            initSymbolManager(it)

            if (hasPermissions(permissions)) {
                showUserLocationOnMap()
            } else {
                requestPermissions(
                    permissions, REQUEST_CODE
                )
            }
        }

    }

    private fun initSymbolManager(style: Style) {
        symbolManager = SymbolManager(mapView, map, style)
        symbolManager.iconAllowOverlap = true
        symbolManager.iconTranslate = arrayOf(-4f, 5f)
        symbolManager.iconRotationAlignment = ICON_ROTATION_ALIGNMENT_VIEWPORT
    }

    private fun addMarkerOnMap(latLng: LatLng) {
        symbolManager.create(
            SymbolOptions()
                .withLatLng(latLng)
                .withIconImage(RESTAURANT_MARKER_ID)
                .withIconSize(1.0f)
        )
    }

    private fun showUserLocationOnMap() {
        viewmodel.initUserLocation()
        observeUserLiveLocation()
        showUserMarkerOnMap()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
        compositeDisposable.dispose()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

}
