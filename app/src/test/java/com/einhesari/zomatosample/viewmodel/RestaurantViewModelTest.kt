package com.einhesari.zomatosample

import android.location.Location
import com.einhesari.zomatosample.model.Restaurant
import com.einhesari.zomatosample.model.RestaurantSearchResponse
import com.einhesari.zomatosample.network.ApiService
import com.einhesari.zomatosample.viewmodel.LocationRepository
import com.einhesari.zomatosample.viewmodel.RestaurantsViewModel
import com.einhesari.zomatosample.viewmodel.FindRestaurantRepository
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.lang.Exception


class RestaurantViewModelTest {
    lateinit var viewModel: RestaurantsViewModel

    @Mock
    lateinit var locationRepository: LocationRepository

    lateinit var findRestaurantRepository: FindRestaurantRepository

    @Mock
    lateinit var apiService: ApiService

    @Mock
    private lateinit var location: Location
    private val latitude = 37.4219983
    private val longitude = -122.084
    private val radius = 1000

    private val restaurantSearchResponse = "{\n" +
            "  \"results_found\": 9900,\n" +
            "  \"results_start\": 0,\n" +
            "  \"results_shown\": 20,\n" +
            "  \"restaurants\": [\n" +
            "    {\n" +
            "      \"restaurant\": {\n" +
            "        \"R\": {\n" +
            "          \"has_menu_status\": {\n" +
            "            \"delivery\": -1,\n" +
            "            \"takeaway\": -1\n" +
            "          },\n" +
            "          \"res_id\": 16844845\n" +
            "        },\n" +
            "        \"apikey\": \"fb8e9264e847fc28a44187844fc9e667\",\n" +
            "        \"id\": \"16844845\",\n" +
            "        \"name\": \"In-N-Out Burger\",\n" +
            "        \"url\": \"https://www.zomato.com/mountain-view-ca/in-n-out-mountain-view?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1\",\n" +
            "        \"location\": {\n" +
            "          \"address\": \"1159 North Rengstorff, Mountain View 94043\",\n" +
            "          \"locality\": \"Mountain View\",\n" +
            "          \"city\": \"Mountain View\",\n" +
            "          \"city_id\": 10847,\n" +
            "          \"latitude\": \"37.4208666667\",\n" +
            "          \"longitude\": \"-122.0933138889\",\n" +
            "          \"zipcode\": \"94043\",\n" +
            "          \"country_id\": 216,\n" +
            "          \"locality_verbose\": \"Mountain View, Mountain View\"\n" +
            "        },\n" +
            "        \"switch_to_order_menu\": 0,\n" +
            "        \"cuisines\": \"Burger, Fast Food\",\n" +
            "        \"timings\": \"10:30 AM to 1 AM (Mon, Tue, Wed, Thu, Sun), 10:30 AM to 1:30 AM (Fri-Sat)\",\n" +
            "        \"average_cost_for_two\": 15,\n" +
            "        \"price_range\": 1,\n" +
            "        \"currency\": \"\$\",\n" +
            "        \"highlights\": [\n" +
            "          \"Lunch\",\n" +
            "          \"Credit Card\",\n" +
            "          \"Dinner\",\n" +
            "          \"Takeaway Available\",\n" +
            "          \"Outdoor Seating\",\n" +
            "          \"Kid Friendly\",\n" +
            "          \"Beer\"\n" +
            "        ],\n" +
            "        \"offers\": [],\n" +
            "        \"opentable_support\": 0,\n" +
            "        \"is_zomato_book_res\": 0,\n" +
            "        \"mezzo_provider\": \"OTHER\",\n" +
            "        \"is_book_form_web_view\": 0,\n" +
            "        \"book_form_web_view_url\": \"\",\n" +
            "        \"book_again_url\": \"\",\n" +
            "        \"thumb\": \"https://b.zmtcdn.com/data/res_imagery/16844845_CHAIN_c6a203688e8e293bac7fbd42dbd589c3_c.png?fit=around%7C200%3A200&crop=200%3A200%3B%2A%2C%2A\",\n" +
            "        \"user_rating\": {\n" +
            "          \"aggregate_rating\": \"3.8\",\n" +
            "          \"rating_text\": \"Good\",\n" +
            "          \"rating_color\": \"9ACD32\",\n" +
            "          \"rating_obj\": {\n" +
            "            \"title\": {\n" +
            "              \"text\": \"3.8\"\n" +
            "            },\n" +
            "            \"bg_color\": {\n" +
            "              \"type\": \"lime\",\n" +
            "              \"tint\": \"600\"\n" +
            "            }\n" +
            "          },\n" +
            "          \"votes\": \"99\"\n" +
            "        },\n" +
            "        \"all_reviews_count\": 14,\n" +
            "        \"photos_url\": \"https://www.zomato.com/mountain-view-ca/in-n-out-mountain-view/photos?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1#tabtop\",\n" +
            "        \"photo_count\": 13,\n" +
            "        \"photos\": [\n" +
            "          {\n" +
            "            \"photo\": {\n" +
            "              \"id\": \"u_4MDUwNjE3Njk1M\",\n" +
            "              \"url\": \"https://b.zmtcdn.com/data/reviews_photos/896/d4e427faab0e141f18f1647c376c9896_1574028527.jpg\",\n" +
            "              \"thumb_url\": \"https://b.zmtcdn.com/data/reviews_photos/896/d4e427faab0e141f18f1647c376c9896_1574028527.jpg?impolicy=newcropandfit&cropw=3024&croph=3024&cropoffsetx=0&cropoffsety=151&cropgravity=NorthWest&fitw=200&fith=200&fittype=ignore\",\n" +
            "              \"user\": {\n" +
            "                \"name\": \"HungryDiner2.0\",\n" +
            "                \"zomato_handle\": \"mar_vinus\",\n" +
            "                \"foodie_level\": \"Super Foodie\",\n" +
            "                \"foodie_level_num\": 11,\n" +
            "                \"foodie_color\": \"f58552\",\n" +
            "                \"profile_url\": \"https://www.zomato.com/mar_vinus?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1\",\n" +
            "                \"profile_image\": \"https://b.zmtcdn.com/data/user_profile_pictures/053/2ed6a536f60e5bbca16b94ea3749d053.jpg?fit=around%7C100%3A100&crop=100%3A100%3B%2A%2C%2A\",\n" +
            "                \"profile_deeplink\": \"zomato://u/31855217\"\n" +
            "              },\n" +
            "              \"res_id\": 16844845,\n" +
            "              \"caption\": \"\",\n" +
            "              \"timestamp\": 1574028528,\n" +
            "              \"friendly_time\": \"4 months ago\",\n" +
            "              \"width\": 3024,\n" +
            "              \"height\": 4032\n" +
            "            }\n" +
            "          },\n" +
            "          {\n" +
            "            \"photo\": {\n" +
            "              \"id\": \"u_MzMjA0MDM3NzQz\",\n" +
            "              \"url\": \"https://b.zmtcdn.com/data/reviews_photos/20d/c25da2a733e24b8b284738d818f5620d_1574028528.jpg\",\n" +
            "              \"thumb_url\": \"https://b.zmtcdn.com/data/reviews_photos/20d/c25da2a733e24b8b284738d818f5620d_1574028528.jpg?impolicy=newcropandfit&cropw=3024&croph=3024&cropoffsetx=0&cropoffsety=789&cropgravity=NorthWest&fitw=200&fith=200&fittype=ignore\",\n" +
            "              \"user\": {\n" +
            "                \"name\": \"HungryDiner2.0\",\n" +
            "                \"zomato_handle\": \"mar_vinus\",\n" +
            "                \"foodie_level\": \"Super Foodie\",\n" +
            "                \"foodie_level_num\": 11,\n" +
            "                \"foodie_color\": \"f58552\",\n" +
            "                \"profile_url\": \"https://www.zomato.com/mar_vinus?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1\",\n" +
            "                \"profile_image\": \"https://b.zmtcdn.com/data/user_profile_pictures/053/2ed6a536f60e5bbca16b94ea3749d053.jpg?fit=around%7C100%3A100&crop=100%3A100%3B%2A%2C%2A\",\n" +
            "                \"profile_deeplink\": \"zomato://u/31855217\"\n" +
            "              },\n" +
            "              \"res_id\": 16844845,\n" +
            "              \"caption\": \"\",\n" +
            "              \"timestamp\": 1574028528,\n" +
            "              \"friendly_time\": \"4 months ago\",\n" +
            "              \"width\": 3024,\n" +
            "              \"height\": 4032\n" +
            "            }\n" +
            "          },\n" +
            "          {\n" +
            "            \"photo\": {\n" +
            "              \"id\": \"u_TMzODIyOTkzMzA\",\n" +
            "              \"url\": \"https://b.zmtcdn.com/data/reviews_photos/59c/aa61481db5b269ef63b0b7a40632c59c_1564365403.jpg\",\n" +
            "              \"thumb_url\": \"https://b.zmtcdn.com/data/reviews_photos/59c/aa61481db5b269ef63b0b7a40632c59c_1564365403.jpg?impolicy=newcropandfit&cropw=750&croph=750&cropoffsetx=0&cropoffsety=256&cropgravity=NorthWest&fitw=200&fith=200&fittype=ignore\",\n" +
            "              \"user\": {\n" +
            "                \"name\": \"R.J\",\n" +
            "                \"zomato_handle\": \"Prudil\",\n" +
            "                \"foodie_level\": \"Super Foodie\",\n" +
            "                \"foodie_level_num\": 10,\n" +
            "                \"foodie_color\": \"f58552\",\n" +
            "                \"profile_url\": \"https://www.zomato.com/Prudil?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1\",\n" +
            "                \"profile_image\": \"https://b.zmtcdn.com/data/user_profile_pictures/738/b7b3993cb6d728e89a90a20a7db35738.jpg?fit=around%7C100%3A100&crop=100%3A100%3B%2A%2C%2A\",\n" +
            "                \"profile_deeplink\": \"zomato://u/33891296\"\n" +
            "              },\n" +
            "              \"res_id\": 16844845,\n" +
            "              \"caption\": \"\",\n" +
            "              \"timestamp\": 1564365404,\n" +
            "              \"friendly_time\": \"8 months ago\",\n" +
            "              \"width\": 750,\n" +
            "              \"height\": 1334\n" +
            "            }\n" +
            "          },\n" +
            "          {\n" +
            "            \"photo\": {\n" +
            "              \"id\": \"u_Njk3MDUzMTQ4OT\",\n" +
            "              \"url\": \"https://b.zmtcdn.com/data/reviews_photos/175/e6ce13db85084d0e94dd1a376756c175.jpg\",\n" +
            "              \"thumb_url\": \"https://b.zmtcdn.com/data/reviews_photos/175/e6ce13db85084d0e94dd1a376756c175.jpg?impolicy=newcropandfit&cropw=1200&croph=1200&cropoffsetx=0&cropoffsety=315&cropgravity=NorthWest&fitw=200&fith=200&fittype=ignore\",\n" +
            "              \"user\": {\n" +
            "                \"name\": \"Brie\",\n" +
            "                \"foodie_level\": \"Big Foodie\",\n" +
            "                \"foodie_level_num\": 6,\n" +
            "                \"foodie_color\": \"ffae4f\",\n" +
            "                \"profile_url\": \"https://www.zomato.com/users/brie-22613410?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1\",\n" +
            "                \"profile_image\": \"https://b.zmtcdn.com/data/user_profile_pictures/070/120ea617e7362b3b019a590c915c2070.jpg?fit=around%7C100%3A100&crop=100%3A100%3B%2A%2C%2A\",\n" +
            "                \"profile_deeplink\": \"zomato://u/22613410\"\n" +
            "              },\n" +
            "              \"res_id\": 16844845,\n" +
            "              \"caption\": \"In-N-Out Burger\",\n" +
            "              \"timestamp\": 1373008276,\n" +
            "              \"friendly_time\": \"Jul 05, 2013\",\n" +
            "              \"width\": 1530,\n" +
            "              \"height\": 2048\n" +
            "            }\n" +
            "          }\n" +
            "        ],\n" +
            "        \"menu_url\": \"https://www.zomato.com/mountain-view-ca/in-n-out-mountain-view/menu?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1&openSwipeBox=menu&showMinimal=1#tabtop\",\n" +
            "        \"featured_image\": \"https://b.zmtcdn.com/data/res_imagery/16844845_CHAIN_c6a203688e8e293bac7fbd42dbd589c3_c.png\",\n" +
            "        \"has_online_delivery\": 0,\n" +
            "        \"is_delivering_now\": 0,\n" +
            "        \"store_type\": \"\",\n" +
            "        \"include_bogo_offers\": true,\n" +
            "        \"deeplink\": \"zomato://restaurant/16844845\",\n" +
            "        \"is_table_reservation_supported\": 0,\n" +
            "        \"has_table_booking\": 0,\n" +
            "        \"events_url\": \"https://www.zomato.com/mountain-view-ca/in-n-out-mountain-view/events#tabtop?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1\",\n" +
            "        \"phone_numbers\": \"(800) 786-1000\",\n" +
            "        \"all_reviews\": {\n" +
            "          \"reviews\": [\n" +
            "            {\n" +
            "              \"review\": []\n" +
            "            },\n" +
            "            {\n" +
            "              \"review\": []\n" +
            "            },\n" +
            "            {\n" +
            "              \"review\": []\n" +
            "            },\n" +
            "            {\n" +
            "              \"review\": []\n" +
            "            },\n" +
            "            {\n" +
            "              \"review\": []\n" +
            "            }\n" +
            "          ]\n" +
            "        },\n" +
            "        \"establishment\": [\n" +
            "          \"Fast Food\"\n" +
            "        ],\n" +
            "        \"establishment_types\": []\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"restaurant\": {\n" +
            "        \"R\": {\n" +
            "          \"has_menu_status\": {\n" +
            "            \"delivery\": -1,\n" +
            "            \"takeaway\": -1\n" +
            "          },\n" +
            "          \"res_id\": 16840053\n" +
            "        },\n" +
            "        \"apikey\": \"fb8e9264e847fc28a44187844fc9e667\",\n" +
            "        \"id\": \"16840053\",\n" +
            "        \"name\": \"Amber India\",\n" +
            "        \"url\": \"https://www.zomato.com/mountain-view-ca/amber-india-mountain-view?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1\",\n" +
            "        \"location\": {\n" +
            "          \"address\": \"2290 W El Camino Real, Suite 9, Mountain View 94040\",\n" +
            "          \"locality\": \"Mountain View\",\n" +
            "          \"city\": \"Mountain View\",\n" +
            "          \"city_id\": 10847,\n" +
            "          \"latitude\": \"37.3972000000\",\n" +
            "          \"longitude\": \"-122.1045970000\",\n" +
            "          \"zipcode\": \"94040\",\n" +
            "          \"country_id\": 216,\n" +
            "          \"locality_verbose\": \"Mountain View, Mountain View\"\n" +
            "        },\n" +
            "        \"switch_to_order_menu\": 0,\n" +
            "        \"cuisines\": \"Indian\",\n" +
            "        \"timings\": \"11:30 AM to 2:30 PM, 5 PM to 10 PM (Mon-Sun)\",\n" +
            "        \"average_cost_for_two\": 80,\n" +
            "        \"price_range\": 3,\n" +
            "        \"currency\": \"\$\",\n" +
            "        \"highlights\": [\n" +
            "          \"Credit Card\",\n" +
            "          \"Lunch\",\n" +
            "          \"Takeaway Available\",\n" +
            "          \"No Alcohol Available\",\n" +
            "          \"Dinner\",\n" +
            "          \"Wifi\",\n" +
            "          \"Vegetarian Friendly\",\n" +
            "          \"Table booking recommended\",\n" +
            "          \"Indoor Seating\"\n" +
            "        ],\n" +
            "        \"offers\": [],\n" +
            "        \"opentable_support\": 0,\n" +
            "        \"is_zomato_book_res\": 0,\n" +
            "        \"mezzo_provider\": \"OTHER\",\n" +
            "        \"is_book_form_web_view\": 0,\n" +
            "        \"book_form_web_view_url\": \"\",\n" +
            "        \"book_again_url\": \"\",\n" +
            "        \"thumb\": \"\",\n" +
            "        \"user_rating\": {\n" +
            "          \"aggregate_rating\": \"3.8\",\n" +
            "          \"rating_text\": \"Good\",\n" +
            "          \"rating_color\": \"9ACD32\",\n" +
            "          \"rating_obj\": {\n" +
            "            \"title\": {\n" +
            "              \"text\": \"3.8\"\n" +
            "            },\n" +
            "            \"bg_color\": {\n" +
            "              \"type\": \"lime\",\n" +
            "              \"tint\": \"600\"\n" +
            "            }\n" +
            "          },\n" +
            "          \"votes\": \"132\"\n" +
            "        },\n" +
            "        \"all_reviews_count\": 11,\n" +
            "        \"photos_url\": \"https://www.zomato.com/mountain-view-ca/amber-india-mountain-view/photos?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1#tabtop\",\n" +
            "        \"photo_count\": 0,\n" +
            "        \"menu_url\": \"https://www.zomato.com/mountain-view-ca/amber-india-mountain-view/menu?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1&openSwipeBox=menu&showMinimal=1#tabtop\",\n" +
            "        \"featured_image\": \"\",\n" +
            "        \"has_online_delivery\": 0,\n" +
            "        \"is_delivering_now\": 0,\n" +
            "        \"store_type\": \"\",\n" +
            "        \"include_bogo_offers\": true,\n" +
            "        \"deeplink\": \"zomato://restaurant/16840053\",\n" +
            "        \"is_table_reservation_supported\": 0,\n" +
            "        \"has_table_booking\": 0,\n" +
            "        \"events_url\": \"https://www.zomato.com/mountain-view-ca/amber-india-mountain-view/events#tabtop?utm_source=api_basic_user&utm_medium=api&utm_campaign=v2.1\",\n" +
            "        \"phone_numbers\": \"(650) 968-7511\",\n" +
            "        \"all_reviews\": {\n" +
            "          \"reviews\": [\n" +
            "            {\n" +
            "              \"review\": []\n" +
            "            },\n" +
            "            {\n" +
            "              \"review\": []\n" +
            "            },\n" +
            "            {\n" +
            "              \"review\": []\n" +
            "            },\n" +
            "            {\n" +
            "              \"review\": []\n" +
            "            },\n" +
            "            {\n" +
            "              \"review\": []\n" +
            "            }\n" +
            "          ]\n" +
            "        },\n" +
            "        \"establishment\": [\n" +
            "          \"Casual Dining\"\n" +
            "        ],\n" +
            "        \"establishment_types\": []\n" +
            "      }\n" +
            "    }\n" +
            "  ]\n" +
            "}"
    private val city = "Mountain View"
    private val restaurantCount = 2
    private val gson = Gson()
    private val networkErrorDesc = "Some netwrok error"
    private val locationErrorDesc = "Some location api error"
    private lateinit var foundedRestaurants: ArrayList<Restaurant>

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        findRestaurantRepository = FindRestaurantRepository(apiService)
        viewModel = RestaurantsViewModel(locationRepository, findRestaurantRepository)

        `when`(location.longitude).thenReturn(longitude)
        `when`(location.latitude).thenReturn(latitude)
    }

    @Test
    fun getLiveLocationSuccessfully() {
        `when`(locationRepository.getUserLiveLocation()).thenReturn(Observable.just(location))
        val locationObserver = viewModel.getUserLiveLocation().test()

        locationObserver.assertValue {
            it.longitude == longitude &&
                    it.latitude == latitude
        }
    }

    @Test
    fun getNearRestaurantsSuccessfully() {
        val response =
            gson.fromJson(restaurantSearchResponse, RestaurantSearchResponse::class.java)
        `when`(
            apiService.findRestaurant(
                location.latitude.toString(),
                location.longitude.toString(),
                radius.toString()
            )
        ).thenReturn(Single.just(response))
        viewModel.findNearRestaurant(location)
        val nearRestaurantsObserver = viewModel.getRestaurants().test()
        nearRestaurantsObserver.assertValue {
            it.isNotEmpty() &&
                    it.size == restaurantCount &&
                    it.all {
                        it.restaurantLocation.city == city
                    }

        }
    }

    @Test
    fun getErrors() {

        `when`(locationRepository.getlocationErrors()).thenReturn(
            Observable.just(
                Exception(
                    locationErrorDesc
                )
            )
        )

        `when`(
            apiService.findRestaurant(
                location.latitude.toString(),
                location.longitude.toString(),
                radius.toString()
            )
        ).thenReturn(Single.error(Throwable(networkErrorDesc)))


        viewModel.findNearRestaurant(location)
        val errorsObserver = viewModel.errors().test()
        errorsObserver.assertOf {
            errorsObserver.valueCount() == 2 &&
                    errorsObserver.values()[0].message == locationErrorDesc &&
                    errorsObserver.values()[1].message == networkErrorDesc
        }
    }

    @Test
    fun searchRestaurants() {
        val response =
            gson.fromJson(restaurantSearchResponse, RestaurantSearchResponse::class.java)
        `when`(
            apiService.findRestaurant(
                location.latitude.toString(),
                location.longitude.toString(),
                radius.toString()
            )
        ).thenReturn(Single.just(response))
        viewModel.findNearRestaurant(location)
        viewModel.getRestaurants().subscribe {
            foundedRestaurants = it
        }
        val nameSearchQuery = "t b"
        val cuisineSearchQuery = "r, f"
        viewModel.searchRestaurant(nameSearchQuery, foundedRestaurants)
        val nameSearchResult = viewModel.getSearchResult().test()
        nameSearchResult.assertValue {
            it.size == 1 &&
                    it[0].name == "In-N-Out Burger"
        }
        viewModel.searchRestaurant(cuisineSearchQuery, foundedRestaurants)
        val cuisineSearchResult = viewModel.getSearchResult().test()
        cuisineSearchResult.assertValue {
            it.size == 1 &&
                    it[0].cuisines == "Burger, Fast Food"
        }
    }
}
