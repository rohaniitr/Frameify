package com.rohansarkar.frameify.db.face_detection

import android.graphics.RectF
import androidx.room.*

@Dao
interface ImageFrameDao {

    // Insert multiple frames
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFrames(frames: List<ImageFrame>)

    @Query("""
        UPDATE image_frame
        SET tag = :tag
        WHERE image_uri = :imageUri 
          AND frame_left = :frameLeft 
          AND frame_top = :frameTop 
          AND frame_right = :frameRight 
          AND frame_bottom = :frameBottom
    """)
    suspend fun update(imageUri: String, frameLeft: Float, frameTop: Float, frameRight: Float, frameBottom: Float, tag: String)
}
