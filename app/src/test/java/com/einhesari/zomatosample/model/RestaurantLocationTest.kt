package com.einhesari.zomatosample.model

import com.google.gson.Gson
import org.junit.Test

class RestaurantLocationTest {
    private val locationJson = " {\n" +
            "        \"address\": \"1 5th Avenue, New York, NY 10003\",\n" +
            "        \"locality\": \"Greenwich Village\",\n" +
            "        \"city\": \"New York City\",\n" +
            "        \"latitude\": \"40.732013\",\n" +
            "        \"longitude\": \"-73.996155\",\n" +
            "        \"zipcode\": \"10003\",\n" +
            "        \"country_id\": \"216\"\n" +
            "      }"

    private val gson = Gson()

    @Test
     fun testRestaurantLocationModel(){
        val gsonLocation = gson.fromJson(locationJson, RestaurantLocation::class.java)

        val manualLocation = RestaurantLocation(
            address = "1 5th Avenue, New York, NY 10003",
            locality = "Greenwich Village",
            city = "New York City",
            latitude = "40.732013",
            longitude = "-73.996155",
            zipcode = "10003",
            countryId = "216"
        )
        assert(manualLocation.address == gsonLocation.address)
        assert(manualLocation.locality == gsonLocation.locality)
        assert(manualLocation.city == gsonLocation.city)
        assert(manualLocation.latitude == gsonLocation.latitude)
        assert(manualLocation.longitude == gsonLocation.longitude)
        assert(manualLocation.zipcode == gsonLocation.zipcode)
        assert(manualLocation.countryId == gsonLocation.countryId)

    }
}