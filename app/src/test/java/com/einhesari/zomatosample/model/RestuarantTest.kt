package com.einhesari.zomatosample.model

import com.google.gson.Gson
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class RestuarantTest {
    private val singleRestaurantJson = "{\n" +
            "  \"id\": \"16774318\",\n" +
            "  \"name\": \"Otto Enoteca & Pizzeria\",\n" +
            "  \"url\": \"https://www.zomato.com/new-york-city/otto-enoteca-pizzeria-greenwich-village\",\n" +
            "  \"location\": {\n" +
            "    \"address\": \"1 5th Avenue, New York, NY 10003\",\n" +
            "    \"locality\": \"Greenwich Village\",\n" +
            "    \"city\": \"New York City\",\n" +
            "    \"latitude\": \"40.732013\",\n" +
            "    \"longitude\": \"-73.996155\",\n" +
            "    \"zipcode\": \"10003\",\n" +
            "    \"country_id\": \"216\"\n" +
            "  },\n" +
            "  \"average_cost_for_two\": \"60\",\n" +
            "  \"price_range\": \"2\",\n" +
            "  \"currency\": \"\$\",\n" +
            "  \"thumb\": \"https://b.zmtcdn.com/data/pictures/chains/8/16774318/a54deb9e4dbb79dd7c8091b30c642077_featured_thumb.png\",\n" +
            "  \"featured_image\": \"https://d.zmtcdn.com/data/pictures/chains/8/16774318/a54deb9e4dbb79dd7c8091b30c642077_featured_v2.png\",\n" +
            "  \"photos_url\": \"https://www.zomato.com/new-york-city/otto-enoteca-pizzeria-greenwich-village/photos#tabtop\",\n" +
            "  \"menu_url\": \"https://www.zomato.com/new-york-city/otto-enoteca-pizzeria-greenwich-village/menu#tabtop\",\n" +
            "  \"events_url\": \"https://www.zomato.com/new-york-city/otto-enoteca-pizzeria-greenwich-village/events#tabtop\",\n" +
            "  \"user_rating\": {\n" +
            "    \"aggregate_rating\": \"3.7\",\n" +
            "    \"rating_text\": \"Very Good\",\n" +
            "    \"rating_color\": \"5BA829\",\n" +
            "    \"votes\": \"1046\"\n" +
            "  },\n" +
            "  \"has_online_delivery\": \"0\",\n" +
            "  \"is_delivering_now\": \"0\",\n" +
            "  \"has_table_booking\": \"0\",\n" +
            "  \"deeplink\": \"zomato://r/16774318\",\n" +
            "  \"cuisines\": \"Cafe\",\n" +
            "  \"all_reviews_count\": \"15\",\n" +
            "  \"photo_count\": \"18\",\n" +
            "  \"phone_numbers\": \"(212) 228-2930\",\n" +
            "  \"photos\": [\n" +
            "    {\n" +
            "      \"id\": \"u_MjA5MjY1OTk5OT\",\n" +
            "      \"url\": \"https://b.zmtcdn.com/data/reviews_photos/c15/9eb13ceaf6e90129c276ce6ff980bc15_1435111695_640_640_thumb.JPG\",\n" +
            "      \"thumb_url\": \"https://b.zmtcdn.com/data/reviews_photos/c15/9eb13ceaf6e90129c276ce6ff980bc15_1435111695_200_thumb.JPG\",\n" +
            "      \"user\": {\n" +
            "        \"name\": \"John Doe\",\n" +
            "        \"zomato_handle\": \"John\",\n" +
            "        \"foodie_level\": \"Super Foodie\",\n" +
            "        \"foodie_level_num\": \"9\",\n" +
            "        \"foodie_color\": \"f58552\",\n" +
            "        \"profile_url\": \"https://www.zomato.com/john\",\n" +
            "        \"profile_deeplink\": \"zoma.to/u/1170245\",\n" +
            "        \"profile_image\": \"string\"\n" +
            "      },\n" +
            "      \"res_id\": \"16782899\",\n" +
            "      \"caption\": \"#awesome\",\n" +
            "      \"timestamp\": \"1435111770\",\n" +
            "      \"friendly_time\": \"3 months ago\",\n" +
            "      \"width\": \"640\",\n" +
            "      \"height\": \"640\",\n" +
            "      \"comments_count\": \"0\",\n" +
            "      \"likes_count\": \"0\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"all_reviews\": [\n" +
            "    {\n" +
            "      \"rating\": \"5\",\n" +
            "      \"review_text\": \"The best latte I've ever had. It tasted a little sweet\",\n" +
            "      \"id\": \"24127336\",\n" +
            "      \"rating_color\": \"305D02\",\n" +
            "      \"review_time_friendly\": \"2 months ago\",\n" +
            "      \"rating_text\": \"Insane!\",\n" +
            "      \"timestamp\": \"1435507367\",\n" +
            "      \"likes\": \"0\",\n" +
            "      \"user\": {\n" +
            "        \"name\": \"John Doe\",\n" +
            "        \"zomato_handle\": \"John\",\n" +
            "        \"foodie_level\": \"Super Foodie\",\n" +
            "        \"foodie_level_num\": \"9\",\n" +
            "        \"foodie_color\": \"f58552\",\n" +
            "        \"profile_url\": \"https://www.zomato.com/john\",\n" +
            "        \"profile_deeplink\": \"zoma.to/u/1170245\",\n" +
            "        \"profile_image\": \"string\"\n" +
            "      },\n" +
            "      \"comments_count\": \"0\"\n" +
            "    }\n" +
            "  ]\n" +
            "}"

    private val gson = Gson()

    private val id = "16774318"
    private val name = "Otto Enoteca & Pizzeria"
    private val url = "https://www.zomato.com/new-york-city/otto-enoteca-pizzeria-greenwich-village"
    private val averageCostForTwo = "60"
    private val priceRange = "2"
    private val currency = "$"
    private val thumb =
        "https://b.zmtcdn.com/data/pictures/chains/8/16774318/a54deb9e4dbb79dd7c8091b30c642077_featured_thumb.png"
    private val featuredImage =
        "https://d.zmtcdn.com/data/pictures/chains/8/16774318/a54deb9e4dbb79dd7c8091b30c642077_featured_v2.png"
    private val photosUrl =
        "https://www.zomato.com/new-york-city/otto-enoteca-pizzeria-greenwich-village/photos#tabtop"
    private val menuUrl =
        "https://www.zomato.com/new-york-city/otto-enoteca-pizzeria-greenwich-village/menu#tabtop"
    private val eventsUrl =
        "https://www.zomato.com/new-york-city/otto-enoteca-pizzeria-greenwich-village/events#tabtop"
    private val hasOnlineDelivery = "0"
    private val isDeliveringNow = "0"
    private val hasTableBooking = "0"
    private val deeplink = "zomato://r/16774318"
    private val cuisines = "Cafe"
    private val allReviewsCount = "15"
    private val phoneNumbers = "(212) 228-2930"

    private lateinit var gsonRestaurant: Restaurant

    @Mock
    private lateinit var restaurantLocation: RestaurantLocation

    @Mock
    private lateinit var userRating: UserRating

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        //Mock restaurant location
        Mockito.`when`(restaurantLocation.address).thenReturn("1 5th Avenue, New York, NY 10003")
        Mockito.`when`(restaurantLocation.locality).thenReturn("Greenwich Village")
        Mockito.`when`(restaurantLocation.city).thenReturn("New York City")
        Mockito.`when`(restaurantLocation.latitude).thenReturn("40.732013")
        Mockito.`when`(restaurantLocation.longitude).thenReturn("-73.996155")
        Mockito.`when`(restaurantLocation.zipcode).thenReturn("10003")
        Mockito.`when`(restaurantLocation.countryId).thenReturn("216")

        //Mock user raitng
        Mockito.`when`(userRating.aggregateRating).thenReturn(3.7f)
        Mockito.`when`(userRating.ratingText).thenReturn("Very Good")
        Mockito.`when`(userRating.ratingColor).thenReturn("5BA829")
        Mockito.`when`(userRating.votes).thenReturn(1046)

    }

    @Test
    fun testRestaurantModel() {
        val manualRestaurant = Restaurant(
            id = id,
            name = name,
            url = url,
            restaurantLocation = restaurantLocation,
            averageCostForTwo = averageCostForTwo,
            priceRange = priceRange,
            currency = currency,
            thumb = thumb,
            featuredImage = featuredImage,
            photosUrl = photosUrl,
            menuUrl = menuUrl,
            eventsUrl = eventsUrl,
            userRating = userRating,
            hasOnlineDelivery = hasOnlineDelivery,
            isDeliveringNow = isDeliveringNow,
            hasTableBooking = hasTableBooking,
            deeplink = deeplink,
            cuisines = cuisines,
            allReviewsCount = allReviewsCount,
            phoneNumbers = phoneNumbers
        )

        gsonRestaurant = gson.fromJson(singleRestaurantJson, Restaurant::class.java)
        assertManualAndGsonRestauranst(manualRestaurant, gsonRestaurant)
    }

    private fun assertManualAndGsonRestauranst(
        manualRestaurant: Restaurant,
        gsonRestaurant: Restaurant
    ) {
        assert(manualRestaurant.id == gsonRestaurant.id)
        assert(manualRestaurant.name == gsonRestaurant.name)
        assert(manualRestaurant.url == gsonRestaurant.url)

        assert(manualRestaurant.restaurantLocation.address == gsonRestaurant.restaurantLocation.address)
        assert(manualRestaurant.restaurantLocation.city == gsonRestaurant.restaurantLocation.city)
        assert(manualRestaurant.restaurantLocation.countryId == gsonRestaurant.restaurantLocation.countryId)
        assert(manualRestaurant.restaurantLocation.latitude == gsonRestaurant.restaurantLocation.latitude)
        assert(manualRestaurant.restaurantLocation.longitude == gsonRestaurant.restaurantLocation.longitude)
        assert(manualRestaurant.restaurantLocation.locality == gsonRestaurant.restaurantLocation.locality)
        assert(manualRestaurant.restaurantLocation.zipcode == gsonRestaurant.restaurantLocation.zipcode)

        assert(manualRestaurant.averageCostForTwo == gsonRestaurant.averageCostForTwo)
        assert(manualRestaurant.priceRange == gsonRestaurant.priceRange)
        assert(manualRestaurant.currency == gsonRestaurant.currency)
        assert(manualRestaurant.thumb == gsonRestaurant.thumb)
        assert(manualRestaurant.featuredImage == gsonRestaurant.featuredImage)
        assert(manualRestaurant.photosUrl == gsonRestaurant.photosUrl)
        assert(manualRestaurant.menuUrl == gsonRestaurant.menuUrl)
        assert(manualRestaurant.eventsUrl == gsonRestaurant.eventsUrl)

        assert(manualRestaurant.userRating.aggregateRating == gsonRestaurant.userRating.aggregateRating)
        assert(manualRestaurant.userRating.ratingColor == gsonRestaurant.userRating.ratingColor)
        assert(manualRestaurant.userRating.ratingText == gsonRestaurant.userRating.ratingText)
        assert(manualRestaurant.userRating.votes == gsonRestaurant.userRating.votes)

        assert(manualRestaurant.hasOnlineDelivery == gsonRestaurant.hasOnlineDelivery)
        assert(manualRestaurant.isDeliveringNow == gsonRestaurant.isDeliveringNow)
        assert(manualRestaurant.hasOnlineDelivery == gsonRestaurant.hasOnlineDelivery)
        assert(manualRestaurant.hasTableBooking == gsonRestaurant.hasTableBooking)
        assert(manualRestaurant.deeplink == gsonRestaurant.deeplink)
        assert(manualRestaurant.cuisines == gsonRestaurant.cuisines)
        assert(manualRestaurant.allReviewsCount == gsonRestaurant.allReviewsCount)
        assert(manualRestaurant.phoneNumbers == gsonRestaurant.phoneNumbers)

    }
}