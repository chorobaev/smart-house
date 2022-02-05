package io.aikosoft.smarthouse.base

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.OnFailureListener
import okhttp3.ResponseBody
import org.json.JSONObject
import javax.inject.Inject

class CommonViewModel @Inject constructor(
    private val app: Application
) : ViewModel(), OnFailureListener {

    override fun onFailure(exception: Exception) {
        _error.value = exception.message
    }

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    companion object {

        private fun ResponseBody.getErrorMessage(): String? {
            return try {
                val jsonObject = JSONObject(string())
                jsonObject.getString("error")
            } catch (e: Exception) {
                e.message
            }
        }
    }
}