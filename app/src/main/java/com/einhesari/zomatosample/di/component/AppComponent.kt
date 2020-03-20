package com.einhesari.zomatosample.di.component

import android.content.Context
import com.einhesari.zomatosample.App
import com.einhesari.zomatosample.di.module.ActivityBuildersModule
import com.einhesari.zomatosample.di.module.ApiModule
import com.einhesari.zomatosample.di.module.ViewModelFactoryModule
import com.einhesari.zomatosample.di.module.ViewModelModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule

@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        ActivityBuildersModule::class,
        ViewModelFactoryModule::class,
        ViewModelModule::class,
        ApiModule::class
    ]
)

interface AppComponent : AndroidInjector<App> {

    @Component.Factory
    interface Factory {

        fun create(@BindsInstance context: Context): AppComponent

    }
}