package com.example.mydiary.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mydiary.data.database.AppDatabase
import com.example.mydiary.data.repository.EntryRepository
import com.example.mydiary.data.entities.Entry
import com.example.mydiary.data.entities.EntryImage
import com.example.mydiary.data.repository.EntryImageRepository
import com.example.mydiary.utils.manager.ToastManager
import com.example.mydiary.utils.manager.ToastType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Date

data class EntryUiState(
    var id: Long = 0L,
    val title: String = "",
    val content: String = "",
    val date: Date = Date(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class EntryViewModel(application: Application) : AndroidViewModel(application) {
    private val entryRepository: EntryRepository
    private val entryImageRepository: EntryImageRepository
    private val _uiState = MutableStateFlow(EntryUiState())
    val uiState: StateFlow<EntryUiState> = _uiState.asStateFlow()
    private val _entryImages = MutableStateFlow<List<Uri>>(emptyList())
    val entryImages: StateFlow<List<Uri>> = _entryImages.asStateFlow()
    private val _originalImages = MutableStateFlow<Set<String>>(emptySet())

    init {
        var database: AppDatabase? = null
        try {
            database = AppDatabase.getDatabase(application)
        } catch (e: Exception) {
            Log.e("Database", "Error initializing database", e)
        }

        entryRepository = EntryRepository(database!!.entryDao())
        entryImageRepository = EntryImageRepository(database.entryImageDao())
    }

    suspend fun updateImages(newImages: List<Uri>) {
        _entryImages.value = newImages
    }

    fun loadImagesForEntry(entryId: Long) {
        viewModelScope.launch {
            try {
                entryImageRepository.getImagePathsForEntry(entryId)
                    .collect { paths ->
                        _originalImages.value = paths.toSet()
                        _entryImages.value = paths.map { it.toUri() }
                    }
            } catch (e: Exception) {
                Log.e("EntryViewModel", "Error loading images for entry", e)
                _entryImages.value = emptyList()
                _originalImages.value = emptySet()
            }
        }
    }

    suspend fun loadEntries(): List<Entry> {
        return entryRepository.getAllEntries().first()
    }

    fun updateTitle(title: String) {
        _uiState.value = _uiState.value.copy(title = title)
    }

    fun updateContent(content: String) {
        _uiState.value = _uiState.value.copy(content = content)
    }

    fun updateEntry(entryId: Long, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                entryRepository.updateEntry(Entry(entryId, _uiState.value.title, _uiState.value.content, _uiState.value.date))
                ToastManager.showToast("Entry was edited successfully", ToastType.Info)
                onSuccess()
            } catch (e: Exception){
                Log.e("EntryViewModel", "Error updating entry",e)
            }
        }
    }

    fun deleteEntry(entry: Entry, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                entryRepository.deleteEntry(entry)
                ToastManager.showToast("Successfully deleted", ToastType.Success)
                onSuccess()
            } catch (e: Exception){
                ToastManager.showToast("There was an error deleting the entry!", ToastType.Error)
                Log.e("EntryViewModel", "Error deleting entry",e)
            }
        }
    }

    fun saveEntry(onSuccess: (entryId: Long) -> Unit) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                
                val entry = Entry(
                    title = _uiState.value.title,
                    content = _uiState.value.content,
                    date = _uiState.value.date
                )
                
                val entryId: Long = entryRepository.insertEntry(entry)
                _uiState.value = _uiState.value.copy(isLoading = false)
                ToastManager.showToast("Successfully added entry", ToastType.Success)
                onSuccess(entryId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to save entry"
                )
            }
        }
    }

    suspend fun saveImage(entryId: Long, uri: Uri) {
        try {
            val uriString = uri.toString()
            // Only save if it's not in the original images set
            if (!_originalImages.value.contains(uriString)) {
                val entryImage = EntryImage(
                    entryId = entryId,
                    imagePath = uriString
                )
                entryImageRepository.insertEntryImage(entryImage)
            }
        } catch (e: Exception) {
            Log.e("EntryViewModel", "Error saving image", e)
        }
    }


    suspend fun deleteImageByUri(uri: Uri) {
        entryImageRepository.deleteEntryImageByUri(uri.toString())
        val currentImages = _entryImages.value.toMutableList()
        currentImages.remove(uri)
        _entryImages.value = currentImages
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
