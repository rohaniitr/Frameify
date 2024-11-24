package com.rohansarkar.frameify.db.face_detection

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "face_detection", indices = [Index(value = ["image_uri"], unique = true)])
data class FaceDetection(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "image_uri") val imageUri: String,
    @ColumnInfo(name = "contains_face") val containsFace: Boolean? = null // Null if detection not done
)