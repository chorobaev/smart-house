package io.aikosoft.smarthouse.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.aikosoft.smarthouse.base.CommonViewModel
import io.aikosoft.smarthouse.base.ViewModelFactory
import io.aikosoft.smarthouse.di.util.ViewModelKey
import io.aikosoft.smarthouse.ui.main.MainViewModel

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(CommonViewModel::class)
    abstract fun bindCommonViewModel(commonViewModel: CommonViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(mainViewModel: MainViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}