package com.einhesari.zomatosample.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
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

        (activity as AppCompatActivity).supportActionBar?.show()
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setHasOptionsMenu(true)

        arguments?.let {
            selectedRestaurant =
                it.getParcelable(getString(R.string.selected_restauratn_bundle_key))
            binding.restaurant = selectedRestaurant
            binding.addressTv.isSelected = true
            binding.restaurantNameTv.isSelected = true
            binding.restaurantCuisineTv.isSelected = true
            (activity as AppCompatActivity).supportActionBar?.title = selectedRestaurant?.name

        } ?: also {
            Toast.makeText(context, R.string.sth_went_wrong, Toast.LENGTH_LONG).show()
        }
        binding.host = this
    }

    fun onCallPressed(view: View) {
        selectedRestaurant?.let {
            val callIntent = Intent(Intent.ACTION_DIAL)
            callIntent.data = Uri.parse("tel:${it.phoneNumbers}")
            startActivity(callIntent)
        }
    }

    fun onNavigatePressed(view: View) {
        selectedRestaurant?.let {
            val navigateIntent = Intent(Intent.ACTION_VIEW)
            navigateIntent.data =
                Uri.parse(
                    getString(R.string.google_map_url).format(
                        it.restaurantLocation.latitude,
                        it.restaurantLocation.longitude
                    )
                )
            startActivity(navigateIntent)
        }
    }

    private fun onSharePressed() {
        selectedRestaurant?.let {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = intentType
            val shareMessage = "\n Checkout ${it.name} in Zomato! \r\n ${it.url}"
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            startActivity(shareIntent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_share, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        return if (id == R.id.action_share) {
            onSharePressed()
            true
        } else super.onOptionsItemSelected(item)
    }
}
