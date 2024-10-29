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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mydiary.dto.Entry
import com.example.mydiary.ui.theme.MyDiaryTheme
import com.example.mydiary.utils.DateUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {

    val entries: List<Entry> = listOf(
        Entry("Title 1", "Content 1", Date()),
        Entry("Title 2", "Content 2", Date())
    )

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
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search"
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = Color.White,
                            navigationIconContentColor = Color.White,
                            actionIconContentColor = Color.White
                        )
                    )
                },
                content = { paddingValues ->
                    EntryTable(
                        Modifier
                            .padding(paddingValues)
                            .fillMaxSize()
                    )
                }
            )
            AddButton(onClick = { /* Handle add entry click */ })
        }
    }

    data class MonthHeader(val monthYear: String)
    sealed class EntryListItem {
        data class Header(val monthHeader: MonthHeader) : EntryListItem()
        data class EntryItem(val entry: Entry) : EntryListItem()
    }

    @Composable
    fun EntryTable(modifier: Modifier = Modifier) {

        val groupedItems = entries
            .sortedByDescending { it.date }
            .groupBy { entry ->
                val formatter = SimpleDateFormat("MMMM yyyy", Locale.ENGLISH)
                formatter.format(entry.date)
            }
            .flatMap { (monthYear, monthEntries) ->
                listOf(EntryListItem.Header(MonthHeader(monthYear))) +
                        monthEntries.map { EntryListItem.EntryItem(it) }
            }

        LazyColumn(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.Start,
        ) {
            items(groupedItems.size) { index ->
                when (val item = groupedItems[index]) {
                    is EntryListItem.Header -> {
                        Text(
                            text = item.monthHeader.monthYear,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp, horizontal = 8.dp)
                        )
                        HorizontalDivider()
                    }
                    is EntryListItem.EntryItem -> {
                        EntryRow(item.entry)
                    }
                }
            }
        }
    }

    @Composable
    fun EntryRow(entry: Entry) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically // Added to center-align content vertically
        ) {
            // Title and date
            Text(
                text = entry.title + " - " + DateUtils.reformatDateString(entry.date.toString()),
                modifier = Modifier.weight(1f) // This will push the buttons to the right
            )

            IconButton(
                onClick = { /* TODO: Handle read click */ }
            ) {
                Icon(
                    imageVector = Icons.Filled.DateRange,
                    contentDescription = "Read entry",
                    tint = MaterialTheme.colorScheme.tertiary
                )
            }

            IconButton(
                onClick = { /* TODO: Handle edit click */ }
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit entry",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            IconButton(
                onClick = { /* TODO: Handle delete click */ }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete entry",
                    tint = MaterialTheme.colorScheme.error
                )
            }
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