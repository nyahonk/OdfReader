package com.nyahonk.odfreader.domain

import android.content.Context
import android.net.Uri
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidFileCache @Inject constructor(private val context: Context) {

    fun getCacheDirectory(): File {
        return context.cacheDir.apply {
            File(this, "cache")
            mkdirs()
        }
    }

    fun getCacheFileUri(): Uri {
        // hex hex!
        return Uri.parse("content://com.nyahonk.odfreader.provider/cache/document.odt")
    }

    fun getCacheFile() = File(getCacheDirectory(), "document.odt")

    fun cleanUp() {
        val cache = getCacheDirectory()
        cache.list()?.let {files ->
            files.forEach {
                try {
                    if (it != "document.odt") {
                        File(cache, it).delete()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}