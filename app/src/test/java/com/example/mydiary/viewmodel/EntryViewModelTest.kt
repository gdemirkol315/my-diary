package com.example.mydiary.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.mydiary.data.dao.EntryDao
import com.example.mydiary.data.dao.EntryImageDao
import com.example.mydiary.data.database.AppDatabase
import com.example.mydiary.data.entities.Entry
import com.example.mydiary.utils.manager.ToastManager
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class EntryViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @MockK
    private lateinit var application: Application

    @MockK
    private lateinit var database: AppDatabase

    @MockK
    private lateinit var entryDao: EntryDao

    @MockK
    private lateinit var entryImageDao: EntryImageDao

    private lateinit var viewModel: EntryViewModel

    @Before
    fun setup() {
        io.mockk.MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
        
        every { database.entryDao() } returns entryDao
        every { database.entryImageDao() } returns entryImageDao
        every { AppDatabase.getDatabase(application) } returns database
        
        viewModel = EntryViewModel(application)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadEntries returns entries from repository`() = runTest {
        // Given
        val entries = listOf(
            Entry(id = 1, title = "Test 1", content = "Content 1", date = Date()),
            Entry(id = 2, title = "Test 2", content = "Content 2", date = Date())
        )
        every { entryDao.getAllEntries() } returns flowOf(entries)

        // When
        val result = viewModel.loadEntries()

        // Then
        assertEquals(entries, result)
    }

    @Test
    fun `updateTitle updates uiState with new title`() = runTest {
        // Given
        val newTitle = "New Title"

        // When
        viewModel.updateTitle(newTitle)

        // Then
        assertEquals(newTitle, viewModel.uiState.value.title)
    }

    @Test
    fun `updateContent updates uiState with new content`() = runTest {
        // Given
        val newContent = "New Content"

        // When
        viewModel.updateContent(newContent)

        // Then
        assertEquals(newContent, viewModel.uiState.value.content)
    }

    @Test
    fun `saveEntry saves entry and calls onSuccess with entry id`() = runTest {
        // Given
        val expectedId = 1L
        coEvery { entryDao.insertEntry(any()) } returns expectedId

        var successCalled = false
        var returnedId = 0L

        // When
        viewModel.saveEntry { entryId ->
            successCalled = true
            returnedId = entryId
        }

        // Then
        assertTrue("Success callback was not called", successCalled)
        assertEquals("Returned ID does not match expected ID", expectedId, returnedId)
        assertFalse("Loading state should be false", viewModel.uiState.value.isLoading)
        assertNull("Error should be null", viewModel.uiState.value.error)
    }

    @Test
    fun `saveEntry handles error appropriately`() = runTest {
        // Given
        val errorMessage = "Error saving entry"
        coEvery { entryDao.insertEntry(any()) } throws RuntimeException(errorMessage)

        // When
        viewModel.saveEntry { }

        // Then
        assertFalse("Loading state should be false", viewModel.uiState.value.isLoading)
        assertEquals("Error message does not match", errorMessage, viewModel.uiState.value.error)
    }
}
