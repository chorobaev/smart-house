package io.aikosoft.smarthouse.di.module

import dagger.Module
import dagger.android.ContributesAndroidInjector
import io.aikosoft.smarthouse.ui.main.MainActivity

@Module
abstract class ActivityBindingModule {

    @ContributesAndroidInjector
    abstract fun bindMainActivity(): MainActivity
}