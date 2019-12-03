package io.aikosoft.smarthouse.base

import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.OnFailureListener

abstract class BaseViewModel : ViewModel() {

    var onFailureListener: OnFailureListener? = null

}