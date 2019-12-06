package io.aikosoft.smarthouse.di.module

import dagger.Module
import dagger.android.ContributesAndroidInjector
import io.aikosoft.smarthouse.ui.main.MainActivity
import io.aikosoft.smarthouse.ui.mount.MountActivity
import io.aikosoft.smarthouse.ui.mount.MountFragmentBindingModule

@Module
abstract class ActivityBindingModule {

    @ContributesAndroidInjector
    abstract fun bindMainActivity(): MainActivity

    @ContributesAndroidInjector(modules = [MountFragmentBindingModule::class])
    abstract fun bindMountActivity(): MountActivity
}