/*
 * Copyright 2023 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rohansarkar.frameify.ui_widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.rohansarkar.frameify.R
import com.rohansarkar.frameify.model.Image
import com.rohansarkar.frameify.model.ImageMetadata
import com.rohansarkar.frameify.util.scaleDown
import kotlin.math.min

internal class OverlayView(context: Context?, attrs: AttributeSet?) :
    View(context, attrs) {

    lateinit var onBoxClickListener: (ImageMetadata) -> Unit
    private var image: Image? = null
    private var boxPaint = Paint()
    private var textBackgroundPaint = Paint()
    private var textPaint = Paint()

    private var scaleFactor: Float = 1f

    private var bounds = Rect()

    init {
        initPaints()
    }

    fun clear() {
        image = null
        textPaint.reset()
        textBackgroundPaint.reset()
        boxPaint.reset()
        invalidate()
        initPaints()
    }

    private fun initPaints() {
        textBackgroundPaint.color = Color.BLACK
        textBackgroundPaint.style = Paint.Style.FILL
        textBackgroundPaint.textSize = 50f

        textPaint.color = Color.WHITE
        textPaint.style = Paint.Style.FILL
        textPaint.textSize = 50f

        boxPaint.color = ContextCompat.getColor(context!!, R.color.purple_200)
        boxPaint.strokeWidth = 8F
        boxPaint.style = Paint.Style.STROKE
    }

    @SuppressLint("DefaultLocale")
    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        image?.let {
            for (metadata in it.metadata) {
                val boundingBox = metadata.frame

                val top = (boundingBox.top * scaleFactor)
                val bottom = (boundingBox.bottom * scaleFactor)
                val left = boundingBox.left * scaleFactor
                val right = boundingBox.right * scaleFactor

                // Draw bounding box around detected faces
                val drawableRect = RectF(left, top, right, bottom)
                canvas.drawRect(drawableRect, boxPaint)

                if(metadata.tag.isNotEmpty()) {
                    // Create text to display alongside detected faces
                    val drawableText = metadata.tag

                    // Draw rect behind display text
                    textBackgroundPaint.getTextBounds(
                        drawableText,
                        0,
                        drawableText.length,
                        bounds
                    )
                    val textWidth = bounds.width()
                    val textHeight = bounds.height()
                    canvas.drawRect(
                        left,
                        top,
                        left + textWidth + Companion.BOUNDING_RECT_TEXT_PADDING,
                        top + textHeight + Companion.BOUNDING_RECT_TEXT_PADDING,
                        textBackgroundPaint
                    )

                    // Draw text for detected face
                    canvas.drawText(
                        drawableText,
                        left,
                        top + bounds.height(),
                        textPaint
                    )
                }
            }
        }
    }

    fun setResults(image: Image) {
        this.image = image

        // Images, videos and camera live streams are displayed in FIT_START mode. So we need to scale
        // up the bounding box to match with the size that the images/videos/live streams being
        // displayed.
        val bitmap = ImageDecoder.decodeBitmap(
            ImageDecoder.createSource(context.contentResolver, image.uri)
        )
        val scaledBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
            .scaleDown(512F)
        scaleFactor = min(width * 1f / scaledBitmap.width, height * 1f / scaledBitmap.height)

        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val x = event.x
            val y = event.y

            image?.let {
                for (metadata in it.metadata) {
                    val boundingBox = metadata.frame

                    val left = boundingBox.left * scaleFactor
                    val top = boundingBox.top * scaleFactor
                    val right = boundingBox.right * scaleFactor
                    val bottom = boundingBox.bottom * scaleFactor

                    if (x in left..right && y in top..bottom) {
                        // Trigger the click listener if the touch is within the bounding box
                        onBoxClickListener.invoke(metadata)
                        performClick() // Ensure accessibility support
                        return true
                    }
                }
            }
        }
        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        super.performClick() // Call the default implementation
        return true
    }

    companion object {
        private const val BOUNDING_RECT_TEXT_PADDING = 8
    }
}