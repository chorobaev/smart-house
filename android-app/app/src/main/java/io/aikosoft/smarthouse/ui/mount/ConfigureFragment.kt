package io.aikosoft.smarthouse.ui.mount

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import io.aikosoft.smarthouse.R
import io.aikosoft.smarthouse.base.BaseFragment
import io.aikosoft.smarthouse.utility.makeShortToast
import io.aikosoft.smarthouse.utility.toVisibility
import kotlinx.android.synthetic.main.fragment_configure.*

class ConfigureFragment : BaseFragment() {

    private lateinit var viewModel: MountViewModel

    override val layoutRes: Int
        get() = R.layout.fragment_configure


    override fun initViewModel() {
        super.initViewModel()
        viewModel = getViewModel()
    }

    override fun initOnClicks() {
        super.initOnClicks()
        bt_send.setOnClickListener {
            if (!validate()) return@setOnClickListener
            viewModel.sendSsidAndPassword(et_ssid.text.toString(), et_password.text.toString())
        }
    }

    override fun observeViewModel() {
        super.observeViewModel()
        val lifeOwner = this as LifecycleOwner
        with(viewModel) {
            loading.observe(lifeOwner, Observer {
                pb_loading.visibility = it.toVisibility()
            })
        }
    }

    private fun validate(): Boolean {
        val ssid = et_ssid.text.toString()
        if (ssid.isEmpty()) {
            baseActivity.makeShortToast("Provide WiFi name!")
            return false
        }
        return true
    }
}