package com.einhesari.zomatosample.adapter

import android.widget.ImageView
import androidx.appcompat.widget.AppCompatRatingBar
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.einhesari.zomatosample.R

class BindingAdapter {

    companion object {

        val restaurantImagePlaceHolder = R.drawable.ic_restaurant_placeholder

        @BindingAdapter("android:priceRange")
        @JvmStatic
        fun setPriceRange(ratingBar: AppCompatRatingBar, priceRange: String?) {
            priceRange?.let {
                ratingBar.rating = priceRange.toFloat()
            } ?: run {
                ratingBar.rating = 0.0F
            }
        }

        @BindingAdapter("android:imageUrl")
        @JvmStatic
        fun loadImage(view: ImageView, imageUrl: String?) {
            imageUrl?.let {
                Glide.with(view)
                    .setDefaultRequestOptions(
                        RequestOptions().circleCrop()
                    )
                    .load(imageUrl)
                    .placeholder(restaurantImagePlaceHolder)
                    .error(restaurantImagePlaceHolder)
                    .into(view)
            } ?: also {
                view.setImageResource(restaurantImagePlaceHolder)
            }
        }
    }


}