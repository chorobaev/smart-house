package io.aikosoft.smarthouse.ui.mount

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MountFragmentBindingModule {

    @ContributesAndroidInjector
    abstract fun bindConnectionFragment(): ConnectionFragment

    @ContributesAndroidInjector
    abstract fun bindConfigureFragment(): ConfigureFragment
}