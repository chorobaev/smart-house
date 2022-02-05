package io.aikosoft.smarthouse.ui.mount

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import io.aikosoft.smarthouse.R
import io.aikosoft.smarthouse.base.BaseFragment
import io.aikosoft.smarthouse.utility.makeShortToast
import io.aikosoft.smarthouse.utility.toVisibility
import kotlinx.android.synthetic.main.fragment_setup.*

class SetupFragment : BaseFragment() {

    private lateinit var viewModel: MountViewModel

    override val layoutRes: Int
        get() = R.layout.fragment_setup


    override fun initViewModel() {
        super.initViewModel()

        viewModel = getViewModel()
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

    override fun initOnClicks() {
        super.initOnClicks()
        bt_connect.setOnClickListener {
            if (validate()) {
                viewModel.mountModule(et_module_name.text.toString())
            }
        }
    }

    private fun validate(): Boolean {
        val name = et_module_name.text.toString()
        if (name.isEmpty()) {
            baseActivity.makeShortToast("Define name of the module")
            return false
        }
        return true
    }
}