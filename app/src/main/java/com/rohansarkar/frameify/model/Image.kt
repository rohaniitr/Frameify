package com.rohansarkar.frameify.model

import android.net.Uri
import java.io.Serializable

data class Image(
    val uri: Uri,
    val metadata: List<ImageMetadata>,
    var isSelected: Boolean = false,
) : Serializable
