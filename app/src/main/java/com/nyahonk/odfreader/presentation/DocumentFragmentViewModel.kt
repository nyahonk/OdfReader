package com.nyahonk.odfreader.presentation

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nyahonk.odfreader.domain.UriInteractor
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class DocumentFragmentViewModel @Inject constructor(
   private val errorPublisher: ErrorPublisher,
   private val uriInteractor: UriInteractor
) : ViewModel() {

    private val disposables = CompositeDisposable()

    val uriLiveData: MutableLiveData<Uri> by lazy {
        MutableLiveData<Uri>()
    }

    fun getErrorPublisher(): PublishSubject<Throwable> {
        return errorPublisher.subscribe()
    }

    fun loadFile(uri: String, persistentUri: Boolean) {
        disposables.add(
            uriInteractor.loadUri(Uri.parse(uri), persistentUri)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { uriLiveData.value = it },
                    { errorPublisher.postError(it) }
                )
        )
    }

    override fun onCleared() {
        disposables.dispose()
        super.onCleared()
    }
}