package com.einhesari.zomatosample.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.einhesari.zomatosample.R
import com.einhesari.zomatosample.databinding.FragmentRestaurantBinding
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.Style
import dagger.android.support.DaggerFragment

class RestaurantFragment : DaggerFragment() {

    lateinit var fragmentRestaurantBinding: FragmentRestaurantBinding
    lateinit var fragmentView: View
    lateinit var mapView: MapView

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
        mapView.getMapAsync { mapboxMap ->

            mapboxMap.setStyle(Style.MAPBOX_STREETS) {


            }
        }
    }

}
