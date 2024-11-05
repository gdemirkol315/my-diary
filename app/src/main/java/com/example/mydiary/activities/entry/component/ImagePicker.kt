package com.example.mydiary.activities.entry.component

import android.Manifest
import android.app.Application
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mydiary.utils.manager.ImageManager
import com.example.mydiary.viewmodel.EntryViewModel
import com.google.accompanist.permissions.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ImagePickerComponent(
    images: List<Uri>,
    onImagesChanged: (List<Uri>) -> Unit
) {
    val context = LocalContext.current
    var showImagePicker by remember { mutableStateOf(false) }
    val imageManager = remember { ImageManager(context) }
    val scope = rememberCoroutineScope()

    val cameraPermission = rememberPermissionState(
        permission = Manifest.permission.CAMERA
    )

    val galleryPermission = rememberPermissionState(
        permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
    )
    val viewModel: EntryViewModel = viewModel(
        factory = EntryViewModel.provideFactory(context.applicationContext as Application)
    )

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            if (images.size < 3) {
                onImagesChanged(images + uri)
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success) {
            imageManager.getTempImageUri()?.let { uri ->
                if (images.size < 3) {
                    onImagesChanged(images + uri)
                }
            }
        }
    }

    // Permission handling functions
    fun launchGallery() {
        when (galleryPermission.status) {
            is PermissionStatus.Granted -> {
                galleryLauncher.launch("image/*")
            }
            is PermissionStatus.Denied -> {
                if (galleryPermission.status.shouldShowRationale) {
                    Toast.makeText(
                        context,
                        "Gallery access is needed to select images",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                galleryPermission.launchPermissionRequest()
            }
        }
    }

    fun launchCamera() {
        when (cameraPermission.status) {
            is PermissionStatus.Granted -> {
                try {
                    val uri = imageManager.createTempImageUri()
                    cameraLauncher.launch(uri)
                } catch (e :Exception){
                    Log.e("ImagePickerComponent", "Error creating temp file", e)
                }

            }
            is PermissionStatus.Denied -> {
                if (cameraPermission.status.shouldShowRationale) {
                    // Show explanation why you need this permission
                    Toast.makeText(
                        context,
                        "Camera permission is needed to take photos",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                cameraPermission.launchPermissionRequest()
            }
        }
    }

    Column {
        ImageCarousel(
            images = images,
            isEditMode = true,
            onAddClick = { showImagePicker = true },
            onDeleteClick = { uri ->
                scope.launch { viewModel.deleteImageByUri(uri) }
                onImagesChanged(
                    images.filter { it != uri }
                )
            })
        if (showImagePicker) {
            ImagePickerDialog(
                onDismiss = { showImagePicker = false },
                onGalleryClick = {
                    showImagePicker = false
                    launchGallery()
                },
                onCameraClick = {
                    showImagePicker = false
                    launchCamera()
                }
            )
        }
    }
}
