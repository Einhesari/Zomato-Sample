package com.einhesari.zomatosample.model

import com.google.gson.annotations.SerializedName

data class RestaurantSearchResponse(

    @SerializedName("results_found")
    val resultsFound: String,

    @SerializedName("results_start")
    val resultsStart: String,

    @SerializedName("results_shown")
    val resultsShown: String,

    @SerializedName("restaurants")
    val Restaurants: ArrayList<Restaurant>


)