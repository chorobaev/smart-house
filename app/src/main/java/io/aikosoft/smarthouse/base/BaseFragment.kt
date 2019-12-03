package io.aikosoft.smarthouse.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import dagger.android.support.DaggerFragment
import io.aikosoft.smarthouse.utility.Logger
import javax.inject.Inject

abstract class BaseFragment : DaggerFragment(), Logger {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private var activity: AppCompatActivity? = null

    @get:LayoutRes
    protected abstract val layoutRes: Int

    val baseActivity: AppCompatActivity
        get() = activity!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(layoutRes, container, false)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as AppCompatActivity
    }

    override fun onDetach() {
        super.onDetach()
        activity = null
    }

    protected inline fun <reified M : BaseViewModel> getViewModel(): M =
        ViewModelProvider(baseActivity, viewModelFactory)[M::class.java]
}