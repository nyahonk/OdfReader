package com.nyahonk.odfreader.domain

import com.nyahonk.odfreader.domain.model.CoreOptions
import com.nyahonk.odfreader.domain.model.CoreResult

class CoreWrapper {

    private var lastNativePointer: Long = 0

    init {
        System.loadLibrary("odr-core")
    }

    fun parse(options: CoreOptions?): CoreResult {
        if (lastNativePointer != 0L) {
            throw RuntimeException("do not reuse native pointers for repeated parsing")
        }
        val result: CoreResult = parseNative(options)
        lastNativePointer = result.nativePointer

        when (result.errorCode) {
            0 -> {
            }
            -1 -> result.exception = CoreCouldNotOpenException()
            -2 -> result.exception = CoreEncryptedException()
            -3 -> result.exception = CoreUnknownErrorException()
            -4 -> result.exception = CoreCouldNotTranslateException()
            -5 -> result.exception = CoreUnexpectedFormatException()
            else -> result.exception = CoreUnexpectedErrorCodeException()
        }
        return result
    }

    private external fun parseNative(options: CoreOptions?): CoreResult

    fun backtranslate(options: CoreOptions, htmlDiff: String): CoreResult {
        options.nativePointer = lastNativePointer
        val result = backtranslateNative(options, htmlDiff)
        when (result.errorCode) {
            0 -> {
            }
            -3 -> {
                result.exception = CoreUnknownErrorException()
                result.exception = CoreCouldNotEditException()
                result.exception = CoreCouldNotSaveException()
                result.exception = CoreUnexpectedErrorCodeException()
            }
            -6 -> {
                result.exception = CoreCouldNotEditException()
                result.exception = CoreCouldNotSaveException()
                result.exception = CoreUnexpectedErrorCodeException()
            }
            -7 -> {
                result.exception = CoreCouldNotSaveException()
                result.exception = CoreUnexpectedErrorCodeException()
            }
            else -> result.exception = CoreUnexpectedErrorCodeException()
        }
        return result
    }

    private external fun backtranslateNative(
        options: CoreOptions,
        htmlDiff: String
    ): CoreResult

    fun close() {
        val options = CoreOptions()
        options.nativePointer = lastNativePointer
        closeNative(options)
        lastNativePointer = 0
    }

    private external fun closeNative(options: CoreOptions)

    class CoreCouldNotOpenException : java.lang.RuntimeException()

    class CoreEncryptedException : java.lang.RuntimeException()

    class CoreCouldNotTranslateException : java.lang.RuntimeException()

    class CoreUnexpectedFormatException : java.lang.RuntimeException()

    class CoreUnexpectedErrorCodeException : java.lang.RuntimeException()

    class CoreUnknownErrorException : java.lang.RuntimeException()

    class CoreCouldNotEditException : java.lang.RuntimeException()

    class CoreCouldNotSaveException : java.lang.RuntimeException()
}