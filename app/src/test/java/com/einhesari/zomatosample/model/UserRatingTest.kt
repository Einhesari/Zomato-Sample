package com.einhesari.zomatosample.model

import com.google.gson.Gson
import org.junit.Test

class UserRatingTest {
    private val ratingJson = "{\n" +
            "        \"aggregate_rating\": \"3.7\",\n" +
            "        \"rating_text\": \"Very Good\",\n" +
            "        \"rating_color\": \"5BA829\",\n" +
            "        \"votes\": \"1046\"\n" +
            "      }"

    private val gson = Gson()

    @Test
    fun testUserRatingModel() {
        val gsonRating = gson.fromJson(ratingJson, UserRating::class.java)

        val manualRating = UserRating(
            aggregateRating = 3.7f,
            ratingText = "Very Good",
            ratingColor = "5BA829",
            votes = 1046
        )
        assert(manualRating.aggregateRating == gsonRating.aggregateRating)
        assert(manualRating.ratingText == gsonRating.ratingText)
        assert(manualRating.ratingColor == gsonRating.ratingColor)
        assert(manualRating.votes == gsonRating.votes)

    }
}