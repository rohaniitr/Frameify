package com.rohansarkar.frameify.db.face_detection

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FaceDetectionDao {
    @Query("SELECT * FROM face_detection WHERE image_uri = :uri")
    suspend fun getByUri(uri: String): FaceDetection?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(faceDetection: FaceDetection)

    @Transaction
    @Query("SELECT * FROM face_detection WHERE contains_face = 1")
    fun getFaceDetectionsWithTagsHavingFace(): Flow<List<FaceDetectionWithTags>>
}
