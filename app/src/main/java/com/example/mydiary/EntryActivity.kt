package com.example.mydiary

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mydiary.dto.Entry
import com.example.mydiary.utils.DateUtils
import com.example.mydiary.viewmodel.EntryViewModel


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

    // Handle error messages
    LaunchedEffect(uiState.error) {
        //onCreate equivalent for composable
        if (entry != null) {
            viewModel.updateTitle(entry.title)
            viewModel.updateContent(entry.content)
        }
        topbarText =  entry?.let { "Edit Entry" } ?: "New Entry"
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

                OutlinedTextField(
                    value = DateUtils.reformatDateString(uiState.date.toString()),
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

                Button(
                    onClick = {
                        if (entry != null && entry.id != 0L) {
                            viewModel.updateEntry(entry.id, onSuccess = onNavigateBack)
                        } else {
                            viewModel.saveEntry(onSuccess = onNavigateBack)
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
                        Text("Save")
                    }
                }
            }
        }
    }
}
