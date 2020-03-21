package com.einhesari.zomatosample.model

import com.google.gson.annotations.SerializedName

data class Restaurant(

    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("url")
    val url: String,

    @SerializedName("location")
    val restaurantLocation: RestaurantLocation,

    @SerializedName("average_cost_for_two")
    val averageCostForTwo: String,

    @SerializedName("price_range")
    val priceRange: String,

    @SerializedName("currency")
    val Currency: String,

    @SerializedName("thumb")
    val Thumb: String,

    @SerializedName("featured_image")
    val featuredImage: String,

    @SerializedName("photos_url")
    val photosUrl: String,

    @SerializedName("menu_url")
    val menuUrl: String,

    @SerializedName("events_url")
    val eventsUrl: String,

    @SerializedName("user_rating")
    val userRating: String,

    @SerializedName("has_online_delivery")
    val hasOnlineDelivery: String,

    @SerializedName("is_delivering_now")
    val isDeliveringNow: String,

    @SerializedName("has_table_booking")
    val hasTableBooking: String,

    @SerializedName("deeplink")
    val deeplink: String,

    @SerializedName("cuisines")
    val cuisines: String,

    @SerializedName("all_reviews_count")
    val allReviewsCount: String,

    @SerializedName("phone_numbers")
    val phoneNumbers: String

)
