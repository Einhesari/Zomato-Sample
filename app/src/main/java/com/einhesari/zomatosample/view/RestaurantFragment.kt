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
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.einhesari.zomatosample.R
import com.einhesari.zomatosample.adapter.RestaurantAdapter
import com.einhesari.zomatosample.databinding.FragmentRestaurantBinding
import com.einhesari.zomatosample.model.Restaurant
import com.einhesari.zomatosample.viewmodel.RestaurantFragmentState
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
    private lateinit var adapter: RestaurantAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var snapHelper: SnapHelper

    private lateinit var compositeDisposable: CompositeDisposable

    private lateinit var mapView: MapView
    private lateinit var map: MapboxMap
    private lateinit var symbolManager: SymbolManager
    private val restauratnMarkerId = "restaurant"
    private var lastLocation: Location? = null
    private val PermissionRequestCode = 1000
    private val defaultMapZoom = 13.0
    private val restaurantFocusMapZoom = 18.0
    private lateinit var allRestaurant: List<Restaurant>
    private lateinit var searchedRestaurant: List<Restaurant>
    private val locationSettingRequestCode = 2000

    private val searchInputDelay = 300L
    private val searchInputDelayTimeUnit = TimeUnit.MILLISECONDS

    @Inject
    lateinit var factory: ViewModelProviderFactory
    private lateinit var viewmodel: RestaurantsViewModel

    private val stateBundleLastLocationKey = "LOCATION_KEY"
    private val stateBundlePositionKey = "POSITION_KEY"

    private var lastVisibleRestaurant: Int = 0

    var permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    private var state: RestaurantFragmentState = RestaurantFragmentState.Loading

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_restaurant, container, false)
        return binding.root

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        compositeDisposable = CompositeDisposable()

        viewmodel = ViewModelProvider(this, factory)[RestaurantsViewModel::class.java]

        binding.restaurantFragment = this

        savedInstanceState?.let {
            lastLocation = it.getParcelable(stateBundleLastLocationKey)
            lastVisibleRestaurant = it.getInt(stateBundlePositionKey)
        }

        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)


        initViewIntraction()
    }

    private fun initViewIntraction() {
        initRecyclerView()
        initSearchBarTextWatcher()
    }

    private fun initDataIntraction() {
        lastLocation?.let {
            showUserMarkerOnMap()
            moveCameraLocation(it, defaultMapZoom)
        } ?: also {
            if (hasPermissions(permissions)) {
                viewmodel.setState(RestaurantFragmentState.PermissionGranted)
            } else {
                viewmodel.setState(RestaurantFragmentState.NeedPermission)
            }
        }

        viewmodel.getState()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                handleState(it)
            }.let {
                compositeDisposable.add(it)
            }
    }

    private fun initRecyclerView() {
        adapter = RestaurantAdapter()
        adapter.selectedRestaurant()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val navItem = Bundle()
                navItem.putParcelable(
                    getString(R.string.selected_restauratn_bundle_key),
                    it
                )
                findNavController().navigate(
                    R.id.action_restaurantFragment_to_restaurantDetailFragment,
                    navItem
                )


            }.let {
                compositeDisposable.add(it)
            }

        restaurant_rv = binding.restaurantRecyclerView
        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        restaurant_rv.layoutManager = linearLayoutManager

        snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(restaurant_rv)

        restaurant_rv.adapter = adapter
        restaurant_rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        lastVisibleRestaurant =
                            linearLayoutManager.findLastCompletelyVisibleItemPosition()
                        if (lastVisibleRestaurant < 0) return

                        val restaurantLocation = Location("Restaurant Location")
                        restaurantLocation.apply {
                            latitude =
                                adapter.currentList[lastVisibleRestaurant].restaurantLocation.latitude.toDouble()
                            longitude =
                                adapter.currentList[lastVisibleRestaurant].restaurantLocation.longitude.toDouble()
                        }

                        moveCameraLocation(restaurantLocation, restaurantFocusMapZoom)
                    }
                }
            }
        })
    }

    private fun initSearchBarTextWatcher() {
        binding.searchEdt
            .textChanges()
            .skipInitialValue()
            .debounce(searchInputDelay, searchInputDelayTimeUnit)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({
                viewmodel.searchRestaurant(it.toString(), allRestaurant)
            }, {

            }).let {
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


    private fun moveCameraLocation(location: Location, zoom: Double) {
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    location.latitude,
                    location.longitude
                ), zoom
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


    override fun onMapReady(mapboxMap: MapboxMap) {
        map = mapboxMap
        map.setStyle(Style.MAPBOX_STREETS)
        {

            it.addImage(
                restauratnMarkerId,
                AppCompatResources.getDrawable(context!!, R.drawable.restaurant_marker)!!
                    .toBitmap(),
                false
            )
            initDataIntraction()
            initSymbolManager(it)

        }

    }

    private fun handleState(state: RestaurantFragmentState) {

        this.state = state
        binding.restaurantProgressBar.hide()

        when (state) {
            is RestaurantFragmentState.PermissionDenied -> {
                Toast.makeText(
                    context,
                    R.string.permission_denied,
                    Toast.LENGTH_LONG
                ).show()
                binding.needRetry = true
            }
            is RestaurantFragmentState.PermissionGranted -> {
                viewmodel.initUserLocation()
            }
            is RestaurantFragmentState.ChangeLocationSettingsDenied -> {
                Toast.makeText(
                    context,
                    R.string.change_location_settings_canceled,
                    Toast.LENGTH_LONG
                ).show()
                binding.needRetry = true
            }
            is RestaurantFragmentState.ChangeLocationSettingsAllowed -> {
                viewmodel.initUserLocation()
            }
            is RestaurantFragmentState.FetchedRestaurantsSuccessfully -> {
                removeAllMarkers()
                allRestaurant = state.restaurants
                allRestaurant.forEach {
                    val latLng =
                        LatLng(
                            it.restaurantLocation.latitude.toDouble(),
                            it.restaurantLocation.longitude.toDouble()
                        )
                    addMarkerOnMap(latLng, it.name)

                }
                adapter.submitList(allRestaurant)
                restaurant_rv.smoothScrollToPosition(lastVisibleRestaurant)
            }
            is RestaurantFragmentState.Error -> {
                val error = state.error
                if (error is ApiException) {
                    handleLocationExceptions(error)
                } else {
                    binding.needRetry = true
                    handleNetworkError(error)
                }
            }
            is RestaurantFragmentState.NeedPermission -> {
                requestPermissions(
                    permissions, PermissionRequestCode
                )
            }
            is RestaurantFragmentState.GotUserLocationSuccessfully -> {
                showUserMarkerOnMap()
                lastLocation = state.location
                lastLocation?.let {
                    viewmodel.findNearRestaurant(it)
                    moveCameraLocation(it, defaultMapZoom)
                }

            }
            is RestaurantFragmentState.SearchedRestaurants -> {
                searchedRestaurant = state.restaurants
                removeAllMarkers()
                searchedRestaurant.forEach {
                    val latLng =
                        LatLng(
                            it.restaurantLocation.latitude.toDouble(),
                            it.restaurantLocation.longitude.toDouble()
                        )
                    addMarkerOnMap(latLng, it.name)
                }
                adapter.submitList(searchedRestaurant)

                //move camera to first restaurant location

                val restaurantLocation = Location("First Restaurant Location")
                restaurantLocation.apply {
                    latitude =
                        searchedRestaurant[0].restaurantLocation.latitude.toDouble()
                    longitude =
                        searchedRestaurant[0].restaurantLocation.longitude.toDouble()
                }
                moveCameraLocation(restaurantLocation, restaurantFocusMapZoom)
            }

            is RestaurantFragmentState.Loading -> {
                binding.restaurantProgressBar.show()

            }
        }
    }

    private fun initSymbolManager(style: Style) {
        symbolManager = SymbolManager(mapView, map, style)
        symbolManager.iconTranslate = arrayOf(-4f, 5f)
        symbolManager.iconRotationAlignment = ICON_ROTATION_ALIGNMENT_VIEWPORT
    }

    private fun addMarkerOnMap(latLng: LatLng, text: String) {
        symbolManager.create(
            SymbolOptions()
                .withLatLng(latLng)
                .withIconImage(restauratnMarkerId)
                .withIconSize(2.0f)
                .withTextField(text)
                .withTextOffset(arrayOf(0f, -1.7f))
        )
    }

    private fun removeAllMarkers() {
        symbolManager.deleteAll()
    }


    fun fabOnClick(view: View) {
        binding.needRetry = false
        when (state) {
            is RestaurantFragmentState.Error -> {
                lastLocation?.let {
                    viewmodel.findNearRestaurant(it)
                }
            }
            RestaurantFragmentState.ChangeLocationSettingsDenied -> {
                viewmodel.initUserLocation()
            }
            RestaurantFragmentState.PermissionDenied -> {
                requestPermissions(
                    permissions, PermissionRequestCode
                )
            }
            else -> {
                lastLocation?.let {
                    moveCameraLocation(it, defaultMapZoom)
                }
            }
        }
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
        Toast.makeText(
            context,
            getString(R.string.find_restaurants_failed) + throwable.message,
            Toast.LENGTH_LONG
        ).show()
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
                viewmodel.setState(RestaurantFragmentState.PermissionGranted)
            } else {
                viewmodel.setState(RestaurantFragmentState.PermissionDenied)
            }
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            locationSettingRequestCode -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        viewmodel.setState(RestaurantFragmentState.ChangeLocationSettingsAllowed)
                    }

                    Activity.RESULT_CANCELED -> {
                        viewmodel.setState(RestaurantFragmentState.ChangeLocationSettingsDenied)
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
        outState.putParcelable(stateBundleLastLocationKey, lastLocation)
        outState.putInt(stateBundlePositionKey, lastVisibleRestaurant)
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.dispose()
    }
    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

}
