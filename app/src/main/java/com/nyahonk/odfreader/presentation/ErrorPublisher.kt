package com.nyahonk.odfreader.presentation

import io.reactivex.subjects.PublishSubject

class ErrorPublisher {

    private val errorObserver = PublishSubject.create<Throwable>()

    fun subscribe() = errorObserver

    fun postError(throwable: Throwable) = errorObserver.onNext(throwable)
}