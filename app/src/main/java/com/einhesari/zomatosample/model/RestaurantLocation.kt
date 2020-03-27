package com.einhesari.zomatosample.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RestaurantLocation(

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
    val countryId: String
) : Parcelable
