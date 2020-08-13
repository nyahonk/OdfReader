package com.nyahonk.odfreader.domain.model

import android.net.Uri
import android.os.Parcelable
import com.nyahonk.odfreader.domain.FileLoader
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Result(
    var loaderType: FileLoader.LoaderType? = null,
    var options: Options? = null,
    var partTitles: MutableList<String> = LinkedList(),
    var partUris: MutableList<Uri> = LinkedList()
) : Parcelable