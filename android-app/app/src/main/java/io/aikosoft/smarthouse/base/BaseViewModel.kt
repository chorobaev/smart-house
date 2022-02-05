package io.aikosoft.smarthouse.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.OnFailureListener
import io.aikosoft.smarthouse.utility.Logger
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

abstract class BaseViewModel : ViewModel(), Logger {

    var onFailureListener: OnFailureListener? = null

    private var disposable: CompositeDisposable? = CompositeDisposable()
    protected val mLoading = MutableLiveData<Boolean>()

    val loading: LiveData<Boolean> get() = mLoading

    protected fun <T> Single<T>.request(block: (T) -> Unit = {}): Single<T> {
        mLoading.value = true
        disposable!!.add(
            this
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<T>() {
                    override fun onSuccess(t: T) {
                        block(t)
                        mLoading.value = false
                    }

                    override fun onError(e: Throwable) {
                        onFailureListener?.onFailure(Exception(e))
                        mLoading.value = false
                        e.printStackTrace()
                    }
                })
        )
        return this
    }

    override fun onCleared() {
        super.onCleared()
        disposable?.clear()
        disposable = null
    }
}