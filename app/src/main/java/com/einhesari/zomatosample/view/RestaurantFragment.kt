package com.einhesari.zomatosample.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.einhesari.zomatosample.R
import com.einhesari.zomatosample.adapter.RestaurantAdapter
import com.einhesari.zomatosample.databinding.FragmentRestaurantBinding
import com.einhesari.zomatosample.model.Restaurant
import com.einhesari.zomatosample.viewmodel.RestaurantsViewModel
import com.einhesari.zomatosample.viewmodel.ViewModelProviderFactory
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.jakewharton.rxbinding3.widget.textChanges
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
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import com.mapbox.mapboxsdk.style.layers.Property.*
import dagger.android.support.DaggerFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class RestaurantFragment : DaggerFragment(), OnMapReadyCallback {


    private lateinit var binding: FragmentRestaurantBinding
    private lateinit var restaurant_rv: RecyclerView
    private val adapter: RestaurantAdapter = RestaurantAdapter()
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var foundedRestaurant = ArrayList<Restaurant>()
    private val snapHelper = LinearSnapHelper()
    private val locationSettingRequestCode = 2000

    private val compositeDisposable = CompositeDisposable()
    private lateinit var mapView: MapView
    private lateinit var map: MapboxMap

    private lateinit var symbolManager: SymbolManager
    private val restauratnMarkerId = "restaurant"
    private var lastLocation: Location? = null
    private val PermissionRequestCode = 1000
    private val defaultMapZoom = 13.0

    private val searchInputDelay = 300L
    private val searchInputDelayTimeUnit = TimeUnit.MILLISECONDS

    @Inject
    lateinit var factory: ViewModelProviderFactory
    private lateinit var viewmodel: RestaurantsViewModel


    var permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    enum class FabViewState {
        InternetNotConnected,
        PermissionDenied,
        ChangeLocationSettingsDenied,
        FetchRestaurantsFailed,
        Default
    }

    private var fabViewState = FabViewState.Default

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_restaurant, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewmodel = ViewModelProvider(this, factory)[RestaurantsViewModel::class.java]

        binding.restaurantFragment = this

        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        initRecyclerView()
        initDataIntraction()

    }

    private fun initDataIntraction() {
        getNearRestuarants()
        observeErrors()
        searchRestaurant()
        observeSearchResult()
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
                    addMarkerOnMap(latLng, it.name)

                }
                adapter.submitList(it)
                foundedRestaurant = it
                changeFabToDefault()
            }
            .let {
                compositeDisposable.add(it)
            }
    }

    private fun initRecyclerView() {
        restaurant_rv = binding.restaurantRecyclerView
        linearLayoutManager = LinearLayoutManager(context!!, LinearLayoutManager.HORIZONTAL, false)
        restaurant_rv.layoutManager = linearLayoutManager
        restaurant_rv.adapter = adapter
        snapHelper.attachToRecyclerView(restaurant_rv)
        restaurant_rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        val restaurantPosition =
                            linearLayoutManager.findLastCompletelyVisibleItemPosition()
                        if (restaurantPosition < 0) return

                        val restaurantLocation = Location("Restaurant Location")
                        restaurantLocation.apply {
                            latitude =
                                foundedRestaurant[restaurantPosition].restaurantLocation.latitude.toDouble()
                            longitude =
                                foundedRestaurant[restaurantPosition].restaurantLocation.longitude.toDouble()
                        }

                        moveCameraLocation(restaurantLocation, null)
                    }
                }
            }
        })

    }

    private fun observeUserLiveLocation() {
        viewmodel.getUserLiveLocation()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                lastLocation?.also { lastLocation ->
                    if (viewmodel.needToMoveCamera(it, lastLocation)) {
                        moveCameraLocation(it, defaultMapZoom)
                    }
                    this.lastLocation = it
                } ?: run {
                    lastLocation = it
                    moveCameraLocation(it, defaultMapZoom)
                    viewmodel.findNearRestaurant(it)
                }

            }
            .let {
                compositeDisposable.add(it)
            }
    }

    private fun observeSearchResult() {
        viewmodel.getSearchResult()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it.size > 0)
                    adapter.submitList(it)
                else {
                    adapter.submitList(foundedRestaurant)
                    Toast.makeText(context, R.string.no_restaurant_found, Toast.LENGTH_LONG).show()
                }
            }.let {
                compositeDisposable.add(it)
            }

    }

    private fun observeErrors() {
        viewmodel.errors()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it is ApiException) {
                    handleLocationExceptions(it)
                } else {
                    handleNetworkError(it)
                }

            }.let {
                compositeDisposable.add(it)
            }
    }

    private fun searchRestaurant() {
        binding.searchEdt
            .textChanges()
            .skipInitialValue()
            .debounce(searchInputDelay, searchInputDelayTimeUnit)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.isNotBlank()) {
                    viewmodel.searchRestaurant(it.toString(), foundedRestaurant)

                } else {
                    handleEmptySearchQuery()
                }
            }, {

            }).let {
                compositeDisposable.add(it)
            }

    }

    private fun handleEmptySearchQuery() {
        adapter.submitList(foundedRestaurant)
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


    private fun moveCameraLocation(location: Location, zoom: Double?) {
        zoom?.let {
            map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        location.latitude,
                        location.longitude
                    ), zoom
                )
            )
        } ?: run {
            map.animateCamera(
                CameraUpdateFactory.newLatLng(
                    LatLng(
                        location.latitude,
                        location.longitude
                    )
                )
            )
        }


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


    override fun onMapReady(mapboxMap: MapboxMap) {
        map = mapboxMap
        map.setStyle(Style.MAPBOX_STREETS) {

            it.addImage(
                restauratnMarkerId,
                AppCompatResources.getDrawable(context!!, R.drawable.restaurant_marker)!!
                    .toBitmap(),
                false
            )

            initSymbolManager(it)

            if (hasPermissions(permissions)) {
                showUserLocationOnMap()
            } else {
                requestPermissions(
                    permissions, PermissionRequestCode
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

    private fun addMarkerOnMap(latLng: LatLng, text: String) {
        symbolManager.create(
            SymbolOptions()
                .withLatLng(latLng)
                .withIconImage(restauratnMarkerId)
                .withIconSize(2.0f)
                .withTextAnchor(TEXT_ANCHOR_BOTTOM)
                .withTextField(text)
        )
    }

    private fun showUserLocationOnMap() {
        viewmodel.initUserLocation()
        observeUserLiveLocation()
        showUserMarkerOnMap()
    }

    private fun changeFabToDefault() {
        binding.needRetry = false
        fabViewState = FabViewState.Default
    }

    fun fabOnClick(view: View) {
        when (fabViewState) {
            FabViewState.FetchRestaurantsFailed -> {
                lastLocation?.let {
                    viewmodel.findNearRestaurant(it)
                }
            }
            FabViewState.ChangeLocationSettingsDenied -> {
                viewmodel.initUserLocation()

            }
            FabViewState.PermissionDenied -> {
                requestPermissions(
                    permissions, PermissionRequestCode
                )
            }
            FabViewState.InternetNotConnected -> {

            }
            FabViewState.Default -> {
                lastLocation?.let {
                    moveCameraLocation(it, defaultMapZoom)

                }
            }
        }
        changeFabToDefault()
    }

    private fun handleLocationExceptions(exception: ApiException) {
        when (exception.statusCode) {
            LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                val resolvableApiException = exception as ResolvableApiException
                startIntentSenderForResult(
                    resolvableApiException.resolution.intentSender,
                    locationSettingRequestCode,
                    null,
                    0,
                    0,
                    0,
                    null
                )

            }
            LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {

                fabViewState = FabViewState.ChangeLocationSettingsDenied
                binding.needRetry = true

                Toast.makeText(
                    context,
                    R.string.location_settings_change_unavailable,
                    Toast.LENGTH_LONG
                ).show()
            }

        }
    }

    private fun handleNetworkError(throwable: Throwable) {
        fabViewState = FabViewState.FetchRestaurantsFailed
        binding.needRetry = true
        Toast.makeText(context, R.string.find_restaurants_failed, Toast.LENGTH_LONG).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode.equals(PermissionRequestCode)) {
            if (grantResults.all {
                    it == PackageManager.PERMISSION_GRANTED
                }) {
                showUserLocationOnMap()
                changeFabToDefault()
            } else {
                fabViewState = FabViewState.PermissionDenied
                binding.needRetry = true
                Toast.makeText(context, R.string.permission_denied, Toast.LENGTH_LONG).show()
            }
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            locationSettingRequestCode -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        changeFabToDefault()
                        viewmodel.initUserLocation()
                    }

                    Activity.RESULT_CANCELED -> {
                        Toast.makeText(
                            context,
                            R.string.change_location_settings_canceled,
                            Toast.LENGTH_LONG
                        ).show()
                        fabViewState = FabViewState.ChangeLocationSettingsDenied
                        binding.needRetry = true
                    }

                }
            }
        }

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
