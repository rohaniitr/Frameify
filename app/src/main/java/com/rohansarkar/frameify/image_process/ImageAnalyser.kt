package com.rohansarkar.frameify.image_process

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.facedetector.FaceDetector
import com.google.mediapipe.tasks.vision.facedetector.FaceDetectorResult
import com.rohansarkar.frameify.util.scaleDown
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Wrapper for ML Kit providing image analysis functionalities.
 * This class uses ML Kit to extract & process information from an image.
 *
 * @param appContext The application context.
 */
class ImageAnalyser(private val appContext: Context) {


    private val modelName = "blaze_face_short_range.tflite"
    private val baseOptionsBuilder = BaseOptions.builder().setModelAssetPath(modelName)

    private val optionsBuilder =
        FaceDetector.FaceDetectorOptions.builder()
            .setBaseOptions(baseOptionsBuilder.build())
            .setMinDetectionConfidence(0.5f)
            .setRunningMode(RunningMode.IMAGE)

    private val options = optionsBuilder.build()

    private val faceDetector: FaceDetector =
        FaceDetector.createFromOptions(appContext, options)

    suspend fun getFaceDetection(image: Uri): FaceDetectorResult? {
        return withContext(Dispatchers.IO) {
            val bitmap = ImageDecoder.decodeBitmap(
                ImageDecoder.createSource(appContext.contentResolver, image)
            )
            val scaledBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
                .scaleDown(512F)
            val mpImage = BitmapImageBuilder(scaledBitmap).build()
            faceDetector.detect(mpImage)
        }
    }
}