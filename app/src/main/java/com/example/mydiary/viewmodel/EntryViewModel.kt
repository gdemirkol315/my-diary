package com.example.mydiary.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mydiary.data.database.AppDatabase
import com.example.mydiary.data.repository.EntryRepository
import com.example.mydiary.dto.Entry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

data class EntryUiState(
    val title: String = "",
    val content: String = "",
    val date: Date = Date(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class EntryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: EntryRepository
    private val _uiState = MutableStateFlow(EntryUiState())
    val uiState: StateFlow<EntryUiState> = _uiState.asStateFlow()

    init {
        var database: AppDatabase? = null
        try {
            database = AppDatabase.getDatabase(application)
        } catch (e: Exception) {
            Log.e("Database", "Error initializing database", e)
            // Handle the error appropriately
        }

        repository = EntryRepository(database!!.entryDao())
    }

    fun updateTitle(title: String) {
        _uiState.value = _uiState.value.copy(title = title)
    }

    fun updateContent(content: String) {
        _uiState.value = _uiState.value.copy(content = content)
    }

    fun saveEntry(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                
                val entry = Entry(
                    title = _uiState.value.title,
                    content = _uiState.value.content,
                    date = _uiState.value.date
                )
                
                repository.insertEntry(entry)
                _uiState.value = _uiState.value.copy(isLoading = false)
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to save entry"
                )
            }
        }
    }

    companion object {
        fun provideFactory(application: Application): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(EntryViewModel::class.java)) {
                        return EntryViewModel(application) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
    }
}
