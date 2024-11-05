package com.example.mydiary.activities.entrydetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mydiary.activities.entry.component.ImageCarousel
import com.example.mydiary.data.entities.Entry
import com.example.mydiary.utils.DateUtils
import com.example.mydiary.viewmodel.EntryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryDetailScreen(entry: Entry, viewModel: EntryViewModel, onNavigateBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Entry Details") },
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
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = entry.title,
                onValueChange = { },
                label = { Text("Title") },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = DateUtils.reformatDateString(entry.date.toString()),
                onValueChange = { },
                label = { Text("Date") },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = entry.content,
                onValueChange = { },
                label = { Text("Content") },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(bottom = 16.dp),
                minLines = 5
            )
            viewModel.loadImagesForEntry(entryId = entry.id)
            val images = viewModel.entryImages.collectAsState().value

            ImageCarousel(
                images = images,
                isEditMode = false
            )
        }
    }
}
