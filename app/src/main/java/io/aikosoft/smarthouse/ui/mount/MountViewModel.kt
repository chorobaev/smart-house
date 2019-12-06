package io.aikosoft.smarthouse.ui.mount

import androidx.lifecycle.LiveData
import io.aikosoft.smarthouse.base.BaseViewModel
import io.aikosoft.smarthouse.data.repositories.ModuleRepository
import io.aikosoft.smarthouse.utility.SingleLiveEvent
import javax.inject.Inject

class MountViewModel @Inject constructor(
    private val moduleRepository: ModuleRepository
) : BaseViewModel() {

    private val _permissionsGranted = SingleLiveEvent<Unit>()
    private val _moduleName = SingleLiveEvent<String>()
    private val _disconnectFromWiFi = SingleLiveEvent<Unit>()
    private val _moduleMounted = SingleLiveEvent<Unit>()

    val permissionsGranted: LiveData<Unit> get() = _permissionsGranted
    val shouldConnectToWiFi: LiveData<String> get() = _moduleName
    val shouldDisconnectFromWiFi: LiveData<Unit> get() = _disconnectFromWiFi
    val moduleMounted: LiveData<Unit> get() = _moduleMounted

    fun enableFeatures() {
        _permissionsGranted.call()
    }

    fun connectToModule(moduleName: String) {
        _moduleName.value = moduleName
    }

    fun disconnectFromWiFi() {
        _disconnectFromWiFi.call()
    }

    fun setLoading(loading: Boolean) {
        mLoading.value = loading
    }

    fun sendSsidAndPassword(ssid: String, password: String) {
        val name = ssid
        log("WiFi name: $name")
        moduleRepository.setSsidAndPassword(name, password).request {
            _moduleMounted.call()
        }
    }
}