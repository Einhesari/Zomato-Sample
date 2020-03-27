package com.einhesari.zomatosample.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserRating(

    @SerializedName("aggregate_rating")
    val aggregateRating: Float,

    @SerializedName("rating_text")
    val ratingText: String,

    @SerializedName("rating_color")
    val ratingColor: String,

    @SerializedName("votes")
    val votes: Int

) : Parcelable