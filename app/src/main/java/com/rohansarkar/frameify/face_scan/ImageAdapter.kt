package com.rohansarkar.frameify.face_scan

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rohansarkar.frameify.model.Image
import com.rohansarkar.frameify.ui_widget.ImageThumbnailWidget

/**
 * Adapter to let user interact with their screenshots.
 */
class ImageAdapter : ListAdapter<Image, ImageAdapter.VH>(Diff()) {

    /**
     * Callback for click on item populated via adapter.
     */
    private lateinit var onItemClick: (Image) -> Unit

    fun setItemClickListener(listener: (Image) -> Unit) {
        onItemClick = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ImageThumbnailWidget(parent.context))

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position), onItemClick)
    }

    class VH(private val view: ImageThumbnailWidget) : RecyclerView.ViewHolder(view) {
        fun bind(data: Image?, onItemClick: (Image) -> Unit) {
            this.adapterPosition
            view.bind(data)
            view.setOnClickListener {
                data?.let { onItemClick.invoke(it) }
            }
        }
    }

    class Diff : DiffUtil.ItemCallback<Image>() {
        override fun areItemsTheSame(oldItem: Image, newItem: Image) =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: Image, newItem: Image) =
            oldItem == newItem
    }
}