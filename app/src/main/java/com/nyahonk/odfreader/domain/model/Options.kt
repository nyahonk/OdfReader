package com.nyahonk.odfreader.domain.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Options(
    var originalUri: Uri? = null,
    var cacheUri: Uri? = null,
    var persistentUri: Boolean = false,
    var fileExists: Boolean = false,
    var filename: String? = null,
    var fileType: String? = null,
    var fileExtension: String? = null,
    var password: String? = null,
    var limit: Boolean = false,
    var translatable: Boolean = false
) : Parcelable