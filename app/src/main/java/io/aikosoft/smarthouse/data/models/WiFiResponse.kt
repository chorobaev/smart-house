package io.aikosoft.smarthouse.data.models

class WiFiResponse(
    val status: WiFiStatus,
    val date: String?,
    val error: Throwable?
) {
    companion object {
        fun connecting(): WiFiResponse = WiFiResponse(WiFiStatus.CONNECTING, null, null)
        fun connected(): WiFiResponse = WiFiResponse(WiFiStatus.CONNECTED, null, null)
        fun disconnecting(): WiFiResponse = WiFiResponse(WiFiStatus.DISCONNECTING, null, null)
        fun disconnected(): WiFiResponse = WiFiResponse(WiFiStatus.DISCONNECTED, null, null)
        fun error(error: Throwable): WiFiResponse = WiFiResponse(WiFiStatus.ERROR, null, error)
    }
}