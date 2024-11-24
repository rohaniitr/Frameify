package com.rohansarkar.frameify.image_process

import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Locale

/**
 * Utility for interacting with Image resources from gallery.
 */
class LocalImageHandler {

    /**
     * Function to get all images from a specific directory.
     */
    suspend fun getImagesFromDirectory(directoryPath: String): List<Uri> {
        return withContext(Dispatchers.IO) {
            val imageList = mutableListOf<Uri>()

            // Create a File object representing the directory
            val directory = File(directoryPath)

            // Check if the directory exists and is a directory
            if (directory.exists() && directory.isDirectory) {
                // List all files in the directory
                val files = directory.listFiles()

                // Filter and add only image files to the list
                files?.forEach { file ->
                    if (isImageFile(file)) {
                        val contentUri = Uri.fromFile(file)
                        imageList.add(contentUri)
                    }
                }
            }
            imageList
        }
    }

    /**
     * Function to check if a file is an image file.
     */
    private fun isImageFile(file: File): Boolean {
        val extension = file.extension.lowercase(Locale.ROOT)
        return extension == "jpg" || extension == "jpeg" || extension == "png" || extension == "gif"
                || extension == "bmp" || extension == "webp"
    }
}