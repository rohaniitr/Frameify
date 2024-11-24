package com.rohansarkar.frameify.ui_widget

import android.content.Context
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import com.rohansarkar.frameify.R
import com.rohansarkar.frameify.model.Image
import com.rohansarkar.frameify.util.dpToPixels
import com.rohansarkar.frameify.util.load

/**
 * Item in bottom strip displaying an image.
 */
class ImageThumbnailWidget(context: Context) : AppCompatImageView(context) {

    init {
        layoutParams = ViewGroup.LayoutParams(
            32.dpToPixels(context),
            48.dpToPixels(context),
        )
        val paddingInPx = context.resources.getDimensionPixelSize(R.dimen.padding_4dp)
        setPadding(paddingInPx, paddingTop, paddingInPx, paddingBottom)
    }

    fun bind(data: Image?) {
        load(data?.uri)
    }
}