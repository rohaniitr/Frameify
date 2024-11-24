package com.rohansarkar.frameify.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Decoration to add horizontal spacing between items in [RecyclerView].
 */
class HorizontalSpaceItemDecoration(private val horizontalSpaceWidth: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        outRect.right = horizontalSpaceWidth
    }
}