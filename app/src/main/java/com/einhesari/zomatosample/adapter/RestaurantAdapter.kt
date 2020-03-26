package com.einhesari.zomatosample.adapter

import android.location.Location
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.einhesari.zomatosample.R
import com.einhesari.zomatosample.databinding.RestaurantItemBinding
import com.einhesari.zomatosample.model.Restaurant
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import kotlinx.android.synthetic.main.restaurant_item.view.*

class RestaurantAdapter :
    ListAdapter<Restaurant, RestaurantAdapter.RestaurantViewHolder>(DIFF_CALLBACK()) {


    class DIFF_CALLBACK : DiffUtil.ItemCallback<Restaurant>() {
        override fun areItemsTheSame(oldItem: Restaurant, newItem: Restaurant): Boolean {
            return oldItem.name.equals(newItem.name)
        }

        override fun areContentsTheSame(oldItem: Restaurant, newItem: Restaurant): Boolean {
            return oldItem.name.equals(newItem.name)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        return RestaurantViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.restaurant_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        holder.binding.restaurant = getItem(position)
    }

    class RestaurantViewHolder(val binding: RestaurantItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.restaurant_name_tv.isSelected = true
            binding.root.cuisines_tv.isSelected = true
        }
    }
}