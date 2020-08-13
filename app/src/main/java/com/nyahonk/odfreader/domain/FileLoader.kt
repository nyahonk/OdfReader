package com.nyahonk.odfreader.domain

import com.nyahonk.odfreader.domain.model.Options
import com.nyahonk.odfreader.domain.model.Result
import com.nyahonk.odfreader.presentation.ErrorPublisher

abstract class FileLoader(private val errorPublisher: ErrorPublisher) {

    enum class LoaderType {
        ODF, DOC, OOXML, PDF, ONLINE, RAW, METADATA
    }

    abstract fun loadSync(options: Options) : Result

    fun callOnError(throwable: Throwable) {
        errorPublisher.postError(throwable)
    }
}