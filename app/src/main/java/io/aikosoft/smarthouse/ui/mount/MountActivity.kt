package io.aikosoft.smarthouse.ui.mount

import android.Manifest
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import io.aikosoft.smarthouse.R
import io.aikosoft.smarthouse.base.BaseActivity
import io.aikosoft.smarthouse.data.models.WiFiResponse
import io.aikosoft.smarthouse.data.models.WiFiStatus
import io.aikosoft.smarthouse.utility.ConnectionLiveData
import io.aikosoft.smarthouse.utility.WiFiReceiverManager

class MountActivity : BaseActivity() {

    private lateinit var viewModel: MountViewModel
    private var connectionLiveData: ConnectionLiveData? = null
    private var waitingForConnection: Boolean = false
    private var wifiStatus: WiFiStatus? = null
    private var wiFiReceiverManager: WiFiReceiverManager? = null

    override val layoutRes: Int
        get() = R.layout.activity_mount

    override fun initViewModel() {
        super.initViewModel()
        viewModel = getViewModel()
    }

    override fun initUI(firstInit: Boolean) {
        super.initUI(firstInit)

        if (firstInit) {
            supportFragmentManager.beginTransaction().add(R.id.fm_container, ConnectionFragment())
                .commit()
        }

        requestPermissions()
    }

    private fun requestPermissions() {
        Permissions.check(this,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            R.string.location_rational,
            object : PermissionHandler() {
                override fun onGranted() {
                    enableFeatures()
                }

                override fun onDenied(context: Context?, deniedPermissions: ArrayList<String>?) {
                    super.onDenied(context, deniedPermissions)
                    finish()
                }
            }
        )
    }

    private fun enableFeatures() {
        viewModel.enableFeatures()
        subscribeConnectionLiveData()
        initWiFiManager()
        log("Permissions granted")
    }

    private fun subscribeConnectionLiveData() {
        connectionLiveData = ConnectionLiveData(this)
        connectionLiveData?.observe(this, Observer { connected ->
            if (connected!! && waitingForConnection) {
                waitingForConnection = false
                if (wifiStatus == WiFiStatus.CONNECTED) {
                    wifiConnected()
                } else if (wifiStatus == WiFiStatus.DISCONNECTED) {
                    wifiDisconnected()
                }
            } else if (!connected && wifiStatus == WiFiStatus.CONNECTED) {
                Toast.makeText(
                    this,
                    getString(R.string.error_network_disconnected),
                    Toast.LENGTH_SHORT
                ).show()
                wifiDisconnected()
            }
        })
    }

    private fun initWiFiManager() {
        wiFiReceiverManager = WiFiReceiverManager(application, lifecycle)
        wiFiReceiverManager!!.wifiResponse().observe(this, Observer(this::processWifiResponse))
    }

    private fun processWifiResponse(response: WiFiResponse?) {
        wifiStatus = response?.status
        log("New wifi status: $wifiStatus")
        when (wifiStatus) {
            WiFiStatus.CONNECTED -> {
                if (connectionLiveData!!.hasNetworkConnection()) {
                    wifiConnected()
                } else {
                    waitingForConnection = true
                }
            }
            WiFiStatus.CONNECTING -> {
                viewModel.setLoading(true)
            }
            WiFiStatus.DISCONNECTED -> {
                if (connectionLiveData!!.hasNetworkConnection()) {
                    wifiDisconnected()
                } else {
                    waitingForConnection = true
                }
            }
            WiFiStatus.DISCONNECTING -> {
                viewModel.setLoading(true)
            }
            WiFiStatus.ERROR -> {
                val message = response!!.error?.message.toString()
                Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
                    .show()
            }
        }
    }

    override fun observeViewModel() {
        super.observeViewModel()
        val lifeOwner = this as LifecycleOwner
        with(viewModel) {
            shouldConnectToWiFi.observe(lifeOwner, Observer {
                this@MountActivity.connectToWiFi()
            })

            shouldDisconnectFromWiFi.observe(lifeOwner, Observer {
                this@MountActivity.disconnectFromWiFi()
            })
        }
    }

    private fun connectToWiFi() {
        log("Connecting to wifi")
        try {
            //if (wiFiReceiverManager != null && wifiStatus != WiFiStatus.CONNECTED && wifiStatus != WiFiStatus.CONNECTING) {
            wiFiReceiverManager?.connectWifi("B_2", "1718engineer1718")
            // }
        } catch (e: NullPointerException) {
            log("${e.message}")
        }
    }

    private fun disconnectFromWiFi() {
        if (wifiStatus == WiFiStatus.CONNECTED) {
            wiFiReceiverManager?.disconnectFromWifi()
        }
    }

    private fun wifiConnected() {
        log("wifiConnected")
        viewModel.setLoading(false)
    }

    private fun wifiDisconnected() {
        log("wifiDisconnected")
        viewModel.setLoading(false)
    }
}
