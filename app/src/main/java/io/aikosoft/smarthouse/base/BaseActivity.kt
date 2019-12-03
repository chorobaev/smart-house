package io.aikosoft.smarthouse.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import dagger.android.support.DaggerAppCompatActivity
import io.aikosoft.smarthouse.utility.makeShortToast

import javax.inject.Inject

abstract class BaseActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var commonViewModel: CommonViewModel

    @get:LayoutRes
    protected abstract val layoutRes: Int
    protected open fun initUI(firstInit: Boolean) {}
    protected open fun initViewModel() {}
    protected open fun observeViewModel() {}
    protected open fun initOnClicks() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutRes)

        observeCommonViewModel()
        initViewModel()
        initUI(savedInstanceState == null)
        observeViewModel()
        initOnClicks()
    }

    private fun observeCommonViewModel() {
        commonViewModel = ViewModelProvider(this, viewModelFactory)[CommonViewModel::class.java]
        viewModelFactory.setOnFailureListener(commonViewModel)
        commonViewModel.error.observe(this, Observer {
            makeShortToast(it)
        })
    }

    protected inline fun <reified M : BaseViewModel> getViewModel(): M =
        ViewModelProvider(this, viewModelFactory)[M::class.java]
}