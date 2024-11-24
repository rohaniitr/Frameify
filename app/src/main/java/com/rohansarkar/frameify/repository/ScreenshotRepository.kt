package com.rohansarkar.frameify.repository

import android.content.Context
import android.graphics.RectF
import android.net.Uri
import android.os.Environment
import com.rohansarkar.frameify.db.AppDatabase
import com.rohansarkar.frameify.db.face_detection.FaceDetection
import com.rohansarkar.frameify.db.face_detection.FaceDetectionWithTags
import com.rohansarkar.frameify.db.face_detection.ImageFrame
import com.rohansarkar.frameify.image_process.ImageAnalyser
import com.rohansarkar.frameify.util.Constant
import com.rohansarkar.frameify.image_process.LocalImageHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import java.io.File


/**
 * Repository for managing screenshots/images and data related to it.
 * This repository provides utility methods for fetching screenshots from the device's gallery.
 */
class ScreenshotRepository(appContext: Context) {


    /**
     * Utility for fetching screenshots.
     */
    private val localImageHandler = LocalImageHandler()

    /**
     * DAO objects for DB interaction.
     */
    private val faceDetectionDao = AppDatabase.getDatabase(appContext).faceDetectionDao()
    private val imageFrameDao = AppDatabase.getDatabase(appContext).imageFrameDao()

    /**
     * Analyser to perform operations on an image.
     */
    private val imageAnalyser = ImageAnalyser(appContext)


    /**
     * Retrieves a list of URIs representing screenshots from the device's gallery.
     *
     * @return A list of [Uri]s representing the screenshots.
     */
    suspend fun getCameraImages(): List<Uri> {
        return localImageHandler.getImagesFromDirectory(
            File(Environment.getExternalStorageDirectory(), Constant.CAMERA_DIRECTORY).path
        )
    }

    // Function to get a Flow of FaceDetectionWithTags from the database
    fun getFaceDetectionWithTagsFlow(): Flow<List<FaceDetectionWithTags>> {
        return faceDetectionDao.getFaceDetectionsWithTagsHavingFace()
    }

    suspend fun analyseImagesAndUpdateDb(images: List<Uri>) {
        // Chunk the images into chunks of 20
        val chunkSize = 20
        val imageChunks = images.chunked(chunkSize)

        // Limit to 5 concurrent coroutines at a time
        val concurrencyLimit = 5

        coroutineScope {
            // Launch 5 coroutines in parallel to process each chunk concurrently
            val deferredResults = mutableListOf<kotlinx.coroutines.Deferred<Unit>>()

            for (i in imageChunks.indices) {
                // Only run 5 concurrent tasks at a time
                if (deferredResults.size >= concurrencyLimit) {
                    // Wait for the first task to finish
                    deferredResults.removeAt(0).await()
                }

                // Launch coroutine for the current chunk
                val deferred = async(Dispatchers.IO) {
                    processImageChunk(imageChunks[i])
                }

                // Add the deferred task to the list
                deferredResults.add(deferred)
            }

            // Wait for all remaining coroutines to complete
            deferredResults.awaitAll()
        }
    }

    // Helper function to process a single chunk of images
    private suspend fun processImageChunk(chunk: List<Uri>) {
        // Process each image in the chunk
        for (image in chunk) {
            processImageIfNotProcessed(image)
        }
    }

    // Helper function to check if an image is already processed, process it if not, and update the database
    private suspend fun processImageIfNotProcessed(image: Uri) {
        val existingFaceDetection = faceDetectionDao.getByUri(image.toString())

        // Skip processing if the image is already processed
        if (existingFaceDetection != null) {
            return
        }

        // Process the image and detect faces
        val detectionResult = imageAnalyser.getFaceDetection(image)

        // Determine if the image contains a face
        val containsFace = detectionResult?.detections()?.isNotEmpty() ?: false

        // Save the FaceDetection result to the database
        val faceDetection = FaceDetection(
            imageUri = image.toString(),
            containsFace = if (containsFace) true else null
        )
        faceDetectionDao.insert(faceDetection)

        // If the image contains faces, save the detections as ImageFrame entries
        if (containsFace) {
            detectionResult?.detections()?.map { detection ->
                val frame = detection.boundingBox()
                ImageFrame(
                    imageUri = image.toString(),
                    frameLeft = frame.left,
                    frameTop = frame.top,
                    frameRight = frame.right,
                    frameBottom = frame.bottom,
                    tag = ""
                )
            }?.let {
                imageFrameDao.insertFrames(it)
            }
        }
    }

    suspend fun updateTag(imageUri: Uri, frame: RectF, tagText: String) {
        imageFrameDao.update(
            imageUri = imageUri.toString(),
            frameLeft = frame.left,
            frameTop = frame.top,
            frameRight = frame.right,
            frameBottom = frame.bottom,
            tag = tagText
        )
    }
}