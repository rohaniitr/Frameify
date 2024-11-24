package com.rohansarkar.frameify.util

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide

/**
 * Utility function to convert dp to pixels.
 *
 * @param context The context to access the display metrics.
 * @return The equivalent value in pixels.
 */
fun Int.dpToPixels(context: Context): Int {
    val density = context.resources.displayMetrics.density
    return (this * density + 0.5f).toInt()
}

/**
 * Load image from uri into [ImageView].
 *
 * @param uri : Image uri to load.
 * @param placeholder : Placeholder to display when image is being loaded.
 */
fun ImageView.load(uri: Uri?, @DrawableRes placeholder: Int? = null) {
    val requestBuilder = Glide.with(this).load(uri)

    placeholder?.let { requestBuilder.placeholder(it) }
    requestBuilder.into(this)
}

internal fun Bitmap.scaleDown(targetWidth: Float): Bitmap {
    if (targetWidth >= width) return this
    val scaleFactor = targetWidth / width
    return Bitmap.createScaledBitmap(
        this,
        (width * scaleFactor).toInt(),
        (height * scaleFactor).toInt(),
        false
    )
}
