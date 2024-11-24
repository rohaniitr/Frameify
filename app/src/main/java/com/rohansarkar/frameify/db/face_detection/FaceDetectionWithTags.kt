package com.rohansarkar.frameify.db.face_detection

import android.graphics.RectF
import android.net.Uri
import androidx.room.Embedded
import androidx.room.Relation
import com.rohansarkar.frameify.model.Image
import com.rohansarkar.frameify.model.ImageMetadata

data class FaceDetectionWithTags(
    @Embedded val faceDetection: FaceDetection,
    @Relation(
        parentColumn = "image_uri",
        entityColumn = "image_uri"
    )
    val frames: List<ImageFrame>
) {
    fun toImage(): Image {
        return Image(
            uri = Uri.parse(faceDetection.imageUri),
            metadata = frames.map { frame ->
                ImageMetadata(
                    frame = RectF(frame.frameLeft, frame.frameTop, frame.frameRight, frame.frameBottom),
                    tag = frame.tag
                )
            }
        )
    }
}
