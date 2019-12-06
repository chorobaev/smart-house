package io.aikosoft.smarthouse.ui

import com.google.firebase.database.FirebaseDatabase
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import io.aikosoft.smarthouse.di.component.DaggerApplicationComponent
import javax.inject.Inject

class SmartHouseApp : DaggerApplication() {

    @Inject
    lateinit var firebaseDatabase: FirebaseDatabase

    override fun onCreate() {
        super.onCreate()
        firebaseDatabase.setPersistenceEnabled(true)
        instance = this
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        val component = DaggerApplicationComponent.builder()
            .application(this)
            .build()
        component.inject(this)
        return component
    }

    companion object {
        private var instance: SmartHouseApp? = null

        fun getInstance(): SmartHouseApp = instance!!
    }
}