package io.aikosoft.smarthouse.ui.mount

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.aikosoft.smarthouse.base.BaseViewModel
import io.aikosoft.smarthouse.utility.SingleLiveEvent
import javax.inject.Inject

class MountViewModel @Inject constructor() : BaseViewModel() {

    private val _permissionsGranted = SingleLiveEvent<Unit>()
    private val _connectToWiFi = SingleLiveEvent<Unit>()
    private val _disconnectFromWiFi = SingleLiveEvent<Unit>()
    private val _loading = MutableLiveData<Boolean>()

    val permissionsGranted: LiveData<Unit> get() = _permissionsGranted
    val shouldConnectToWiFi: LiveData<Unit> get() = _connectToWiFi
    val shouldDisconnectFromWiFi: LiveData<Unit> get() = _disconnectFromWiFi
    val loading: LiveData<Boolean> get() = _loading

    fun enableFeatures() {
        _permissionsGranted.call()
    }

    fun connectToWiFi() {
        _connectToWiFi.call()
    }

    fun disconnectFromWiFi() {
        _disconnectFromWiFi.call()
    }

    fun setLoading(loading: Boolean) {
        _loading.value = loading
    }
}