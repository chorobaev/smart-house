package io.aikosoft.smarthouse.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.tasks.OnFailureListener
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class ViewModelFactory @Inject
constructor(
    private val creators: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
) : ViewModelProvider.Factory {

    private var onFailureListener: OnFailureListener? = null

    fun setOnFailureListener(listener: OnFailureListener) {
        onFailureListener = listener
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        var creator: Provider<out ViewModel>? = creators[modelClass]
        if (creator == null) {
            for ((key, value) in creators) {
                if (modelClass.isAssignableFrom(key)) {
                    creator = value
                    break
                }
            }
        }
        requireNotNull(creator) { "Unknown model class $modelClass" }

        val viewModel = creator.get()

        if (viewModel is BaseViewModel) {
            viewModel.onFailureListener = onFailureListener
        }

        try {
            return viewModel as T
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

    }
}