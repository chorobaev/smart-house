package io.aikosoft.smarthouse.data.repositories

import io.aikosoft.smarthouse.data.rest.ModuleClient
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ModuleRepository @Inject constructor(
    private val moduleClient: ModuleClient
) {
    fun setSsidAndPassword(ssid: String, password: String): Single<String> =
        moduleClient.sendWiFi(ssid, password)
}