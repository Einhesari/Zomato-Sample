package com.einhesari.zomatosample.di.module

import com.einhesari.zomatosample.view.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuildersModule {

    @ContributesAndroidInjector(
        modules = [
            MainActivityFragmentsBuilderModule::class
        ]
    )
    abstract fun contributeMainActivity(): MainActivity
}