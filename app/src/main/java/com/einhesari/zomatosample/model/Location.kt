package com.einhesari.zomatosample.model

import com.google.gson.annotations.SerializedName

data class Location(

    @SerializedName("address")
    val address: String,

    @SerializedName("locality")
    val locality: String,

    @SerializedName("city")
    val city: String,

    @SerializedName("latitude")
    val latitude: String,

    @SerializedName("longitude")
    val longitude: String,

    @SerializedName("zipcode")
    val zipcode: String,

    @SerializedName("country_id")
    val country_id: String
)