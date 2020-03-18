package com.einhesari.zomatosample.di.module

import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.ViewModelProvider
import com.einhesari.zomatosample.viewmodel.ViewModelProviderFactory
import dagger.Module

@Module
abstract class ViewModelFactoryModule {
     abstract fun bindViewModelFactory(viewModelProviderFactory: ViewModelProviderFactory): ViewModelProvider.Factory
}