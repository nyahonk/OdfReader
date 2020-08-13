package com.nyahonk.odfreader.domain

import android.net.Uri
import android.webkit.MimeTypeMap
import com.nyahonk.odfreader.domain.model.CoreOptions
import com.nyahonk.odfreader.domain.model.CoreResult
import com.nyahonk.odfreader.domain.model.Options
import com.nyahonk.odfreader.domain.model.Result
import com.nyahonk.odfreader.presentation.ErrorPublisher
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OdfLoader @Inject constructor(
    private val androidFileCache: AndroidFileCache,
    errorPublisher: ErrorPublisher
) : FileLoader(errorPublisher)  {

    private var lastCore: CoreWrapper? = null
    private var lastCoreOptions: CoreOptions? = null

    override fun loadSync(options: Options): Result {

        val result = Result().apply {
            this.options = options
            this.loaderType = LoaderType.ODF
        }

        val cachedFile = androidFileCache.getCacheFile()

        lastCore?.close()

        lastCore = CoreWrapper()

        val cacheDirectory = androidFileCache.getCacheDirectory()
        val fakeHtmlFile = File(cacheDirectory, "odf")

        lastCoreOptions = CoreOptions().apply {
            inputPath = cachedFile.path
            outputPath = fakeHtmlFile.path
            password = options.password
            editable = options.translatable
            ooxml = false
        }

        val coreResult: CoreResult = lastCore!!.parse(lastCoreOptions)
        val coreExtension = coreResult.extension
        // "unnamed" refers to default of Meta::typeToString
        if (coreExtension != null && coreExtension != "unnamed") {
            val fileType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(coreExtension)
            if (fileType != null) {
                options.fileType = fileType
            }
        }
        coreResult.exception?.let {
            throw it
        }

        for (i in coreResult.pageNames.indices) {
            val entryFile = File(fakeHtmlFile.path + i + ".html")
            result.partTitles.add(coreResult.pageNames[i])
            result.partUris.add(Uri.fromFile(entryFile))
        }

        return result
    }

}