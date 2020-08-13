package com.nyahonk.odfreader.domain

import java.io.*

object StreamUtils {

    const val ENCODING: String = "UTF-8"

    @Throws(IOException::class)
    fun copy(src: File?, dst: File?) {
        val inputStream: InputStream = FileInputStream(src)
        copy(inputStream, dst)
    }

    @Throws(IOException::class)
    fun copy(src: File?, outputStream: OutputStream) {
        val inputStream: InputStream = FileInputStream(src)
        copy(inputStream, outputStream)
    }

    @Throws(IOException::class)
    fun copy(inputStream: InputStream, dst: File?) {
        val outputStream: OutputStream = FileOutputStream(dst)
        copy(inputStream, outputStream)
        outputStream.close()
    }

    // taken from: https://stackoverflow.com/a/9293885/198996
    @Throws(IOException::class)
    fun copy(inputStream: InputStream, outputStream: OutputStream) {
        try {
            // Transfer bytes from in to outputStream
            val buf = ByteArray(1024)
            var len: Int
            while (inputStream.read(buf).also { len = it } > 0) {
                outputStream.write(buf, 0, len)
            }
            outputStream.flush()
        } finally {
            inputStream.close()
        }
    }
}