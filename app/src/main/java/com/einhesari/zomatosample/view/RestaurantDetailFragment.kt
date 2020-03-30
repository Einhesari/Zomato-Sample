package com.einhesari.zomatosample.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.einhesari.zomatosample.R
import com.einhesari.zomatosample.databinding.FragmentRestaurantDetailBinding
import com.einhesari.zomatosample.model.Restaurant


class RestaurantDetailFragment : Fragment() {

    lateinit var binding: FragmentRestaurantDetailBinding
    var selectedRestaurant: Restaurant? = null
    val intentType = "text/plain"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_restaurant_detail, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        arguments?.let {
            selectedRestaurant =
                it.getParcelable(getString(R.string.selected_restauratn_bundle_key))
            binding.restaurant = selectedRestaurant
            binding.addressTv.isSelected = true
            binding.restaurantNameTv.isSelected = true
            binding.restaurantCuisineTv.isSelected = true
        } ?: also {
            Toast.makeText(context, R.string.sth_went_wrong, Toast.LENGTH_LONG).show()
        }
        binding.host = this
    }

    fun onBackPressed(view: View) {
        findNavController().navigateUp()
    }

    fun onSharePressed(view: View) {
        selectedRestaurant?.let {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = intentType
            val shareMessage = "\n Checkout ${it.name} in Zomato! \r\n ${it.url}"
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            startActivity(shareIntent)
        }
    }
}
