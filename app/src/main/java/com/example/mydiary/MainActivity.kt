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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mydiary.dto.Entry
import com.example.mydiary.navigation.Screen
import com.example.mydiary.ui.theme.MyDiaryTheme
import com.example.mydiary.utils.DateUtils
import com.example.mydiary.viewmodel.EntryViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    private var entries by mutableStateOf<List<Entry>>(emptyList())
    private lateinit var viewModel: EntryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(
            this,
            EntryViewModel.provideFactory(application)
        )[EntryViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            MyDiaryTheme {
                val navController = rememberNavController()
                
                NavHost(
                    navController = navController,
                    startDestination = Screen.Main.route
                ) {
                    composable(Screen.Main.route) {
                        StartScreen(
                            onNavigateToEntry = {
                                navController.navigate(Screen.Entry.route)
                            },
                            onNavigateToEntryDetail = { entryId ->
                                navController.navigate(Screen.EntryDetail.createEntryDetailRoute(entryId))
                            }
                        )
                    }
                    composable(Screen.Entry.route) {
                        EntryScreen(
                            onNavigateBack = {
                                navController.navigateUp()
                            }
                        )
                    }
                    composable(
                        route = Screen.EntryDetail.route,
                        arguments = listOf(
                            navArgument("entryId") { type = NavType.IntType }
                        )
                    ) { backStackEntry ->
                        val entryId = backStackEntry.arguments?.getInt("entryId") ?: 0
                        val entry = entries[entryId]
                        EntryDetailScreen(
                            entry = entry,
                            onNavigateBack = {
                                navController.navigateUp()
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        CoroutineScope(Dispatchers.Main).launch {
            entries = withContext(Dispatchers.IO) {
                viewModel.loadEntries()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun StartScreen(
        onNavigateToEntry: () -> Unit,
        onNavigateToEntryDetail: (Int) -> Unit
    ) {
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
                        .fillMaxSize(),
                    onNavigateToEntryDetail = onNavigateToEntryDetail
                )
            }
        )
        AddButton(onClick = onNavigateToEntry)
    }

    data class MonthHeader(val monthYear: String)
    sealed class EntryListItem {
        data class Header(val monthHeader: MonthHeader) : EntryListItem()
        data class EntryItem(val entry: Entry, val index: Int) : EntryListItem()
    }

    @Composable
    fun EntryTable(
        modifier: Modifier = Modifier,
        onNavigateToEntryDetail: (Int) -> Unit
    ) {
        val groupedItems = entries
            .mapIndexed { index, entry -> index to entry }
            .sortedByDescending { it.second.date }
            .groupBy { (_, entry) ->
                val formatter = SimpleDateFormat("MMMM yyyy", Locale.ENGLISH)
                formatter.format(entry.date)
            }
            .flatMap { (monthYear, monthEntries) ->
                listOf(EntryListItem.Header(MonthHeader(monthYear))) +
                        monthEntries.map { (index, entry) -> EntryListItem.EntryItem(entry, index) }
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
                        EntryRow(item.entry, item.index, onNavigateToEntryDetail)
                    }
                }
            }
        }
    }

    @Composable
    fun EntryRow(
        entry: Entry,
        index: Int,
        onNavigateToEntryDetail: (Int) -> Unit
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = entry.title + " - " + DateUtils.reformatDateString(entry.date.toString()),
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = { onNavigateToEntryDetail(index) }
            ) {
                Icon(
                    imageVector = Icons.Filled.DateRange,
                    contentDescription = "View entry details",
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
