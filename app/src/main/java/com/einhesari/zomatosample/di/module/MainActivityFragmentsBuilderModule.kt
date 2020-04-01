package com.einhesari.zomatosample.di.module

import com.einhesari.zomatosample.view.RestaurantFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainActivityFragmentsBuilderModule {

    @ContributesAndroidInjector
    abstract fun contributeRestaurantFragment(): RestaurantFragment

}