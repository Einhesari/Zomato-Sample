package com.einhesari.zomatosample.adapter

import android.widget.ImageView
import androidx.appcompat.widget.AppCompatRatingBar
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class BindingAdapter {
    companion object {
        @BindingAdapter("android:priceRange")
        @JvmStatic
        fun setPriceRange(ratingBar: AppCompatRatingBar, priceRange: String?) {
            priceRange?.let {
                ratingBar.rating = priceRange.toFloat()
            }.run {
                ratingBar.rating = 0.0F
            }
        }

        @BindingAdapter("android:imageUrl")
        @JvmStatic
        fun loadImage(view: ImageView, imageUrl: String?) {
            Glide.with(view.context)
                .load(imageUrl).apply(RequestOptions().centerInside())
                .into(view)
        }
    }


}