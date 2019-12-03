package io.aikosoft.smarthouse.ui

import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import io.aikosoft.smarthouse.di.component.DaggerApplicationComponent

class SmartHouseApp : DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        val component = DaggerApplicationComponent.builder()
            .application(this)
            .build()
        component.inject(this)
        return component
    }
}