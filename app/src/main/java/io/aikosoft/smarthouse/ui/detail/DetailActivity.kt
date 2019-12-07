package io.aikosoft.smarthouse.ui.detail

import android.view.View.GONE
import android.widget.CompoundButton
import androidx.lifecycle.Observer
import io.aikosoft.smarthouse.R
import io.aikosoft.smarthouse.base.BaseActivity
import io.aikosoft.smarthouse.data.models.ModuleSmartHouseLiz
import io.aikosoft.smarthouse.utility.HIGH
import io.aikosoft.smarthouse.utility.makeShortToast
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : BaseActivity() {

    private lateinit var viewModel: DetailViewModel
    private var moduleId: String? = null

    override val layoutRes: Int get() = R.layout.activity_detail

    override fun initViewModel() {
        super.initViewModel()
        viewModel = getViewModel()
        moduleId = intent.getStringExtra(EXTRA_MODEL_ID)
    }

    override fun observeViewModel() {
        super.observeViewModel()
        moduleId?.let { moduleId ->
            viewModel.getModule(moduleId).observe(this, Observer {
                it?.let { updateUI(it) }
            })

            viewModel.getHumidity(moduleId).observe(this, Observer {
                it?.let { updateHumidity(it) }
            })

            viewModel.getTemperature(moduleId).observe(this, Observer {
                it?.let { updateTemperature(it) }
            })
        }
    }

    private fun updateUI(module: ModuleSmartHouseLiz) {
        progress_bar.visibility = GONE
        with(module) {
            title = name
            tv_humidity.text = humidity
            tv_temperature.text = temperature
            switch_one.isChecked = R1 == HIGH
            switch_two.isChecked = R2 == HIGH
        }
        initSwitches()
    }

    private fun updateTemperature(temperature: String) {
        tv_temperature.text = temperature
    }

    private fun updateHumidity(humidity: String) {
        val temp = "$humidity%"
        tv_humidity.text = temp
    }

    private fun initSwitches() {
        switch_one.setOnCheckedChangeListener { button, checked ->
            log("Switch one: $checked")
            toggle(button, checked, 1)
        }

        switch_two.setOnCheckedChangeListener { button, checked ->
            log("Switch two: $checked")
            toggle(button, checked, 2)
        }
    }

    private fun toggle(button: CompoundButton, checked: Boolean, id: Int) {
        moduleId?.let {
            val event = viewModel.turnRely(it, id, checked)
            event.observe(this, Observer {
                if (it == true) {
                    makeShortToast("Switched!")
                } else {
                    makeShortToast("Failed to switch!")
                    button.isChecked = !checked
                }
                event.removeObservers(this)
            })
        }
    }

    companion object {
        const val EXTRA_MODEL_ID = "io.aikosoft.smarthouse.module_id"
    }
}
