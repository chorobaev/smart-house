package io.aikosoft.smarthouse.utility

import android.util.Log

interface Logger {

    fun log(msg: String) {
        Log.d("Mylog${javaClass.simpleName}", msg)
    }

    fun errorLog(msg: String) {
        Log.e("Mylog${javaClass.simpleName}", msg)
    }
}