package com.rohansarkar.frameify.db.face_detection

import android.graphics.RectF
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "image_frame")
data class ImageFrame(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "image_uri") val imageUri: String,
    @ColumnInfo(name = "frame_left") val frameLeft: Float,
    @ColumnInfo(name = "frame_top") val frameTop: Float,
    @ColumnInfo(name = "frame_right") val frameRight: Float,
    @ColumnInfo(name = "frame_bottom") val frameBottom: Float,
    @ColumnInfo(name = "tag") val tag: String
) {
    companion object {
        fun from(imageUri: String, frame: RectF, tag: String): ImageFrame {
            return ImageFrame(
                imageUri = imageUri,
                frameLeft = frame.left,
                frameTop = frame.top,
                frameRight = frame.right,
                frameBottom = frame.bottom,
                tag = tag
            )
        }

        fun toRectF(frameLeft: Float, frameTop: Float, frameRight: Float, frameBottom: Float): RectF {
            return RectF(frameLeft, frameTop, frameRight, frameBottom)
        }
    }
}
