package com.einhesari.zomatosample

import com.einhesari.zomatosample.di.component.DaggerAppComponent
import com.mapbox.mapboxsdk.Mapbox
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

class App : DaggerApplication() {

    override fun onCreate() {
        super.onCreate()
        Mapbox.getInstance(applicationContext, getString(R.string.mapbox_access_token))
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.factory().create(this)
    }
}