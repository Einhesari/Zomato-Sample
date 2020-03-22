package com.einhesari.zomatosample.adapter

import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatRatingBar
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class BindingAdapter {
    companion object {

        @DrawableRes
        val placeHolder: Int = com.einhesari.zomatosample.R.drawable.ic_restaurant_placeholder

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
                    .placeholder(placeHolder)
                    .into(view)
            } ?: run {
                view.setImageResource(placeHolder)
            }

        }
    }


}