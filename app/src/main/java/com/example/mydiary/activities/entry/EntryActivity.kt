package com.example.mydiary.activities.entry

import android.app.Application
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mydiary.activities.entry.component.ImagePickerComponent
import com.example.mydiary.data.entities.Entry
import com.example.mydiary.utils.DateUtils
import com.example.mydiary.viewmodel.EntryViewModel
import kotlinx.coroutines.launch
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryScreen(onNavigateBack: () -> Unit, entry: Entry? = null) {
    val context = LocalContext.current
    val viewModel: EntryViewModel = viewModel(
        factory = EntryViewModel.provideFactory(context.applicationContext as Application)
    )
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var topbarText = ""
    val images by viewModel.entryImages.collectAsState()

    LaunchedEffect(Unit) {
        if (entry != null) {
            viewModel.updateTitle(entry.title)
            viewModel.updateContent(entry.content)
            viewModel.loadImagesForEntry(entry.id)
        }
        topbarText = entry?.let { "Edit Entry" } ?: "New Entry"
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(
                message = error,
                duration = SnackbarDuration.Short
            )
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(topbarText) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Home"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                OutlinedTextField(
                    value = uiState.title,
                    onValueChange = { viewModel.updateTitle(it) },
                    label = { Text("Title") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
                var dateValue: Date = uiState.date
                if (entry != null) {
                    dateValue = entry.date
                }
                OutlinedTextField(
                    value = DateUtils.reformatDateString(dateValue.toString()),
                    onValueChange = { },
                    label = { Text("Date") },
                    enabled = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = uiState.content,
                    onValueChange = { viewModel.updateContent(it) },
                    label = { Text("Content") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(bottom = 16.dp),
                    minLines = 5
                )
                val scope = rememberCoroutineScope()
                
                ImagePickerComponent(
                    images = images,
                    onImagesChanged = { newImages -> 
                        scope.launch {
                            viewModel.updateImages(newImages)
                        }
                    }
                )
                
                Button(
                    onClick = {
                        if (entry != null) {
                            viewModel.updateEntry(entry, onSuccess = {
                                scope.launch {
                                    saveImages(viewModel, entry.id, images)
                                }
                                onNavigateBack()
                            })
                        } else {
                            viewModel.saveEntry(onSuccess = { entryId: Long ->
                                scope.launch {
                                    saveImages(viewModel, entryId, images)
                                }
                                onNavigateBack()
                            })
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    enabled = !uiState.isLoading
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Save"
                        )
                        Text("Save")
                    }
                }
            }
        }
    }
}

suspend fun saveImages(viewModel: EntryViewModel, entryId: Long, images: List<Uri>) {
    if (images.isNotEmpty()) {
        for (image in images) {
            viewModel.saveImage(entryId, image)
        }
    }
}
