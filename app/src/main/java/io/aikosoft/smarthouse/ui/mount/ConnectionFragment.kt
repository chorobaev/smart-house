package io.aikosoft.smarthouse.ui.mount

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import io.aikosoft.smarthouse.R
import io.aikosoft.smarthouse.base.BaseFragment
import io.aikosoft.smarthouse.utility.toVisibility
import kotlinx.android.synthetic.main.fragment_connection.*

class ConnectionFragment : BaseFragment() {

    private lateinit var viewModel: MountViewModel

    override val layoutRes: Int
        get() = R.layout.fragment_connection


    override fun initViewModel() {
        super.initViewModel()
        viewModel = getViewModel()
    }

    override fun observeViewModel() {
        super.observeViewModel()
        val lifeOwner = this as LifecycleOwner
        with(viewModel) {
            permissionsGranted.observe(lifeOwner, Observer {
                onPermissionsGranted()
            })

            loading.observe(lifeOwner, Observer {
                pb_loading.visibility = it.toVisibility()
            })
        }
    }

    private fun onPermissionsGranted() {
        bt_mount.isEnabled = true
    }

    override fun initOnClicks() {
        super.initOnClicks()
        bt_mount.setOnClickListener {
            viewModel.connectToWiFi()
        }
    }
}