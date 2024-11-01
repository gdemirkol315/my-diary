package com.example.mydiary.utils.manager

import android.content.Context
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class ImageStorageManager(private val context: Context) {

    // Save image and return its path
    suspend fun saveImage(uri: Uri): String? {
        return withContext(Dispatchers.IO) {
            try {
                // Create a unique filename with timestamp
                val timestamp = System.currentTimeMillis()
                val filename = "image_$timestamp.jpg"

                // Get directory for images
                val imagesDir = File(context.filesDir, "images").apply {
                    if (!exists()) mkdirs()
                }

                // Create the file
                val imageFile = File(imagesDir, filename)

                // Copy the content
                context.contentResolver.openInputStream(uri)?.use { input ->
                    FileOutputStream(imageFile).use { output ->
                        input.copyTo(output)
                    }
                }

                imageFile.absolutePath
            } catch (e: Exception) {
                Log.e("ImageStorageManager", "Error saving image", e)
                null
            }
        }
    }

    // Delete image by path
    suspend fun dieleteImage(path: String) {
        withContext(Dispatchers.IO) {
            try {
                File(path).delete()
            } catch (e: Exception) {
                Log.e("ImageStorageManager", "Error deleting image", e)
            }
        }
    }
}