package com.rohansarkar.frameify.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.rohansarkar.frameify.db.face_detection.FaceDetectionDao
import com.rohansarkar.frameify.db.face_detection.ImageFrameDao
import com.rohansarkar.frameify.db.face_detection.FaceDetection
import com.rohansarkar.frameify.db.face_detection.ImageFrame

internal const val DB_NAME = "frameify_db"

@Database(
    entities = [FaceDetection::class, ImageFrame::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun faceDetectionDao(): FaceDetectionDao
    abstract fun imageFrameDao(): ImageFrameDao

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DB_NAME
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
