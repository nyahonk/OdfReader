package com.nyahonk.odfreader.domain.model

import java.util.*

data class CoreResult(
    var nativePointer: Long = 0,
    var errorCode: Int = 0,
    var exception: Exception? = null,
    var pageNames: List<String> = LinkedList(),
    var outputPath: String? = null,
    var extension: String? = null
)