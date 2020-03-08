/*
 * Copyright (c) 2018 ThanksMister LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.aikosoft.smarthouse.utility

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.net.wifi.WifiManager.*
import android.text.TextUtils
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent

import io.aikosoft.smarthouse.data.models.WiFiResponse

class WiFiReceiverManager(private val application: Application, lifecycle: Lifecycle) :
    LifecycleObserver, Logger {

    private val wifiManager: WifiManager =
        application.getSystemService(Context.WIFI_SERVICE) as WifiManager
    private val wifiResponse = MutableLiveData<WiFiResponse>()

    fun wifiResponse(): MutableLiveData<WiFiResponse> {
        return wifiResponse
    }

    init {
        lifecycle.addObserver(this)
    }

    private val intentFilterForWifiConnectionReceiver: IntentFilter
        get() {
            val randomIntentFilter = IntentFilter(WIFI_STATE_CHANGED_ACTION)
            randomIntentFilter.addAction(NETWORK_STATE_CHANGED_ACTION)
            randomIntentFilter.addAction(SUPPLICANT_STATE_CHANGED_ACTION)
            return randomIntentFilter
        }

    private val wifiConnectionReceiver = object : BroadcastReceiver() {
        override fun onReceive(c: Context, intent: Intent) {
            log("onReceive() called with: intent = [$intent]")
            val action = intent.action
            if (!TextUtils.isEmpty(action)) {
                log("action: $action")
                when (action) {
                    WIFI_STATE_CHANGED_ACTION,
                    SUPPLICANT_STATE_CHANGED_ACTION -> {
                        val wifiInfo = wifiManager.connectionInfo
                        var wirelessNetworkName = wifiInfo.ssid
                        wirelessNetworkName = wirelessNetworkName.replace("\"", "")
                        log("WiFi Info current SSID $wirelessNetworkName")
                        log("WiFi Info Hidden " + wifiInfo.hiddenSSID )
                        log("WiFi Connect To networkSSID $networkSSID")
                        log("WiFi previous NetworkSSID $previousNetworkSSID")
                        log("WiFi Names Equal " + (networkSSID == wirelessNetworkName))
                        log("WiFi $disconnecting, NetworkSSID: $networkSSID;  Name: $wirelessNetworkName")
                        if (networkSSID == wirelessNetworkName && !disconnecting) {
                            log("WiFi connected to $networkSSID")
                            wifiResponse.value = WiFiResponse.connected()
                        } else if (networkSSID != wirelessNetworkName && disconnecting) {
                            wifiResponse.value = WiFiResponse.disconnected()
                        }
                    }
                }
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    internal fun registerYourReceiver() {
        application.registerReceiver(wifiConnectionReceiver, intentFilterForWifiConnectionReceiver)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    internal fun unRegisterYourReceiver() {
        try {
            application.unregisterReceiver(wifiConnectionReceiver)
        } catch (e: IllegalArgumentException) {
            log("${e.message}")
        }
    }

    private fun getNetworkId(networkSSID: String): Int {
        val list = wifiManager.configuredNetworks
        for (i in list) {
            if (i.SSID != null && i.SSID == "\"" + networkSSID + "\"") {
                return i.networkId
            }
        }
        return -1
    }

    fun connectWifi(ssid: String, password: String) {

        if (ssid.isEmpty() || password.isEmpty()) {
            log("onReceive: cannot use connection without passing in a proper wifi SSID and password.")
            return
        }

        networkSSID = ssid
        networkPassword = password

        wifiResponse.value = WiFiResponse.connecting()
        if (!wifiManager.isWifiEnabled) {
            if (!wifiManager.setWifiEnabled(true)) {
                wifiResponse.value = WiFiResponse.disconnected()
                return
            }
        }

        val wifiInfo = wifiManager.connectionInfo
        if (wifiInfo.ssid == networkSSID) {
            wifiResponse.value = WiFiResponse.connected()
            return
        }
        previousNetworkSSID = wifiInfo.ssid
        disconnecting = false
        connectToWifi()
    }

    private fun connectToWifi() {
        log("WiFi connectToWifi")
        val conf = WifiConfiguration()
        conf.SSID = String.format("\"%s\"", networkSSID)
        conf.preSharedKey = String.format("\"%s\"", networkPassword)
        conf.status = WifiConfiguration.Status.ENABLED
        conf.hiddenSSID = true

        networkId = wifiManager.addNetwork(conf)
        if (networkId == -1) {
            networkId = getNetworkId(networkSSID)
        }

        wifiManager.disconnect()
        wifiManager.enableNetwork(networkId, true)
        wifiManager.reconnect()
    }

    fun disconnectFromWifi() {
        log("WiFi disconnectFromWifi")
        disconnecting = true
        wifiResponse.value = WiFiResponse.disconnecting()
        wifiManager.disconnect()
        wifiManager.disableNetwork(networkId)
        wifiManager.removeNetwork(networkId)
        wifiManager.reconnect() // reconnect to previous network
    }

    companion object {
        var disconnecting: Boolean = false
        var networkSSID: String = ""
        var networkPassword: String = ""
        var previousNetworkSSID: String = ""
        var networkId: Int = -1
    }
}