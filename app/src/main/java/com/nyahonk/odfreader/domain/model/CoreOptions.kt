package com.nyahonk.odfreader.domain.model

data class CoreOptions(
    var nativePointer: Long = 0,
    var ooxml: Boolean = false,
    var editable: Boolean = false,
    var password: String? = null,
    var inputPath: String? = null,
    var outputPath: String? = null
)