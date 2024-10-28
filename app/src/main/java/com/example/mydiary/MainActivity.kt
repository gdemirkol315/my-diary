package com.example.mydiary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mydiary.dto.Entry
import com.example.mydiary.ui.theme.MyDiaryTheme
import com.example.mydiary.utils.DateUtils
import java.util.Date


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StartScreen()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Preview
    @Composable
    fun StartScreen() {

        MyDiaryTheme {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("My Diary") },
                        actions = {
                            IconButton(onClick = { /* Handle action click */ }) {
                                Icon(
                                    imageVector = Icons.Default.Search, // Add icons for actions here
                                    contentDescription = "Search"
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary, // Background color of the TopAppBar
                            titleContentColor = Color.White, // Title text color
                            navigationIconContentColor = Color.White, // Navigation icon color
                            actionIconContentColor = Color.White // Action icon color
                        )
                    )
                },
                content = { paddingValues ->
                    EntryTable(
                        Modifier
                            .padding(paddingValues) // Apply padding to avoid overlapping with TopAppBar
                            .fillMaxSize()
                    )
                }
            )
            AddButton(onClick = { /* Handle add entry click */ })
        }


    }

    @Composable
    fun EntryTable(modifier: Modifier = Modifier) {
        val entries: List<Entry> = listOf(
            Entry("Title 1", "Content 1", Date()),
            Entry("Title 2", "Content 2", Date())
        )

        LazyColumn(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.Start,

            ) {
            items(entries.size) { index -> EntryRow(entries[index]) }
        }
    }

    @Composable
    fun EntryRow(entry: Entry) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 8.dp)
        ) {

            Text(
                text = entry.title
                        + " - " + DateUtils.reformatDateString(entry.date.toString()),
            )

        }
        HorizontalDivider()
    }

    @Composable
    fun AddButton(onClick: () -> Unit) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            FloatingActionButton(
                onClick = onClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Entry"
                )
            }
        }
    }
}