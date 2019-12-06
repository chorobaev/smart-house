package io.aikosoft.smarthouse.data.rest

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface ModuleClient {

    @GET("mount")
    fun sendWiFi(
        @Query("ssid") ssid: String,
        @Query("password") password: String
    ): Single<String>
}