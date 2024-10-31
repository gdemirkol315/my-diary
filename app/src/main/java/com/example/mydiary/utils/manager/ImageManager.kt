package com.example.mydiary.utils.manager

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

class ImageManager(private val context: Context) {
    private var tempImageUri: Uri? = null

    fun createTempImageUri(): Uri {
        val tmpFile = File.createTempFile(
            "tmp_image_file",
            ".png",
            context.cacheDir
        ).apply {
            createNewFile()
            deleteOnExit()
        }

        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            tmpFile
        ).also { tempImageUri = it }
    }

    fun getTempImageUri() = tempImageUri
}