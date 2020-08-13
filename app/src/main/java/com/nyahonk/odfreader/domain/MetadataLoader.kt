package com.nyahonk.odfreader.domain

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import com.hzy.libmagic.MagicApi
import com.nyahonk.odfreader.domain.model.Options
import com.nyahonk.odfreader.domain.model.Result
import com.nyahonk.odfreader.presentation.ErrorPublisher
import java.io.*
import java.net.URLConnection
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MetadataLoader @Inject constructor(
    private val context: Context,
    private val androidFileCache: AndroidFileCache,
    errorPublisher: ErrorPublisher
) : FileLoader(errorPublisher) {

    private fun initMagicFromAssets(): Boolean {
        var inputStream: InputStream? = null
        try {
            inputStream = context.assets.open("magic.mgc")
            val length = inputStream.available()
            val buffer = ByteArray(length)
            if (inputStream.read(buffer) > 0) {
                return MagicApi.loadFromBytes(
                    buffer,
                    MagicApi.MAGIC_MIME_TYPE or MagicApi.MAGIC_COMPRESS_TRANSP
                ) == 0
            }
        } catch (e: Throwable) {
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close()
                } catch (e: IOException) {
                }
            }
        }
        return false
    }

    override fun loadSync(options: Options): Result {

        val result = Result().apply {
            this.options = options
            this.loaderType = LoaderType.METADATA
        }

        options.fileType = "N/A"

        var uri = options.originalUri

        // cleanup uri
        if ("/./" == uri.toString().substring(0, 2)) {
            uri = Uri.parse(
                uri.toString().substring(
                    2,
                    uri.toString().length
                )
            )
        }

        // TODO: don't delete file being displayed at the moment, but
        // keep it until the new document has finished loading.
        // this must not delete document.odt!
        androidFileCache.cleanUp()
        val cachedFile: File
        if (uri == androidFileCache.getCacheFileUri()) {
            cachedFile = androidFileCache.getCacheFile()
        } else {
            val cacheDirectory = androidFileCache.getCacheDirectory()
            cachedFile = File(cacheDirectory, "document.odt")

            // delete before creating it again
            cachedFile.delete()
            val stream = context.contentResolver.openInputStream(uri!!)
            StreamUtils.copy(stream!!, cachedFile)
        }

        // if file didn't exist an exception would have been thrown by now
        options.fileExists = true
        options.cacheUri = androidFileCache.getCacheFileUri()

        var filename: String? = null

        try {
            // https://stackoverflow.com/a/38304115/198996
            context.contentResolver.query(
                uri,
                null,
                null,
                null,
                null
            )?.let {
                if (it.moveToFirst()) {
                    val nameIndex = it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME)
                    filename = it.getString(nameIndex)
                    it.close()
                }
            }

        } catch (e: Exception) {
            // "URI does not contain a valid access token." or
            // "Couldn't read row 0, col -1 from CursorWindow. Make sure the Cursor is initialized correctly before accessing data from it."
        }

        if (filename == null) {
            filename = uri.lastPathSegment
        }

        if (filename == null) {
            filename = "N/A"
        }

        options.filename = filename
        val fileSplit = options.filename!!.split("\\.".toRegex()).toTypedArray()
        options.fileExtension = if (fileSplit.isNotEmpty()) fileSplit[fileSplit.size - 1] else "N/A"

        var type: String? = null

        try {
            if (initMagicFromAssets()) {
                type = MagicApi.magicFile(cachedFile.absolutePath)
            }
        } catch (e: Throwable) {

        }

        if (type == null) {
            type = context.contentResolver.getType(uri)
        }

        if (type == null && filename != null) {
            try {
                type = URLConnection.guessContentTypeFromName(filename)
            } catch (e: Exception) {
                // Samsung S7 Edge crashes with java.lang.StringIndexOutOfBoundsException
            }
        }

        if (type == null) {
            try {
                val tempStream: InputStream = FileInputStream(cachedFile)
                type = tempStream.use { tempStream ->
                    URLConnection.guessContentTypeFromStream(tempStream)
                }
            } catch (e: Exception) {

            }
        }

        if (type == null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(options.fileExtension)
        }

        if (type != null) {
            options.fileType = type
        }

        if ("inode/x-empty" == type) {
            throw FileNotFoundException()
        }

        val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(options.fileType)

        if (extension != null) {
            // override extension parsed from filename
            options.fileExtension = extension
        }

        return result
    }
}