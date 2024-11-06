package com.example.mydiary.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.mydiary.data.entities.Entry
import com.example.mydiary.data.repository.EntryRepository
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Date
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [33])
class EntryViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: EntryRepository
    private lateinit var application: Application
    private lateinit var viewModel: EntryViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        // Create mocks
        repository = mockk(relaxed = true)
        application = mockk(relaxed = true)
        
        // Initialize ViewModel with mocked dependencies
        viewModel = EntryViewModel(application)
        
        // Use reflection to inject mocked repository
        val field = EntryViewModel::class.java.getDeclaredField("entryRepository")
        field.isAccessible = true
        field.set(viewModel, repository)
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
        coEvery { repository.getAllEntries() } returns flowOf(entries)

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
        val entrySlot = slot<Entry>()
        coEvery { repository.insertEntry(capture(entrySlot)) } returns expectedId

        var successCalled = false
        var returnedId = 0L

        // When
        viewModel.saveEntry { entryId ->
            successCalled = true
            returnedId = entryId
        }
        
        // Wait for all coroutines to complete
        advanceUntilIdle()

        // Then
        assertTrue(successCalled, "Success callback was not called")
        assertEquals(expectedId, returnedId, "Returned ID does not match expected ID")
        assertFalse(viewModel.uiState.value.isLoading, "Loading state should be false")
        assertNull(viewModel.uiState.value.error, "Error should be null")
    }

    @Test
    fun `saveEntry handles error appropriately`() = runTest {
        // Given
        val errorMessage = "Error saving entry"
        coEvery { repository.insertEntry(any()) } throws RuntimeException(errorMessage)

        // When
        viewModel.saveEntry { }
        
        // Wait for all coroutines to complete
        advanceUntilIdle()

        // Then
        assertFalse(viewModel.uiState.value.isLoading, "Loading state should be false")
        assertEquals(errorMessage, viewModel.uiState.value.error, "Error message does not match")
    }
}
