package com.einhesari.zomatosample.di.module

import androidx.lifecycle.ViewModel
import com.einhesari.zomatosample.di.scope.ViewModelKey
import com.einhesari.zomatosample.viewmodel.RestaurantsViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(RestaurantsViewModel::class)
    abstract fun bindRestaurantsViewModel(restaurantsViewModel: RestaurantsViewModel): ViewModel
}