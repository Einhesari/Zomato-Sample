package com.einhesari.zomatosample.model

import com.google.gson.annotations.SerializedName

data class RestaurantWrapper(
    @SerializedName("restaurant")
    val restaurant: Restaurant
)