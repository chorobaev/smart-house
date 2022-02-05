package io.aikosoft.smarthouse.di.component

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import dagger.android.support.DaggerApplication
import io.aikosoft.smarthouse.di.module.ActivityBindingModule
import io.aikosoft.smarthouse.di.module.ApplicationModule
import io.aikosoft.smarthouse.di.module.ContextModule
import io.aikosoft.smarthouse.di.module.FirebaseModule
import io.aikosoft.smarthouse.ui.SmartHouseApp
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        ContextModule::class,
        AndroidSupportInjectionModule::class,
        ApplicationModule::class,
        ActivityBindingModule::class,
        FirebaseModule::class
    ]
)
interface ApplicationComponent : AndroidInjector<DaggerApplication> {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): ApplicationComponent
    }

    fun inject(application: SmartHouseApp)
}