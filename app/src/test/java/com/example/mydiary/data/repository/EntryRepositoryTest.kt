package com.example.mydiary.data.repository

import app.cash.turbine.test
import com.example.mydiary.data.dao.EntryDao
import com.example.mydiary.data.entities.Entry
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals
import java.util.Date

class EntryRepositoryTest {
    private lateinit var entryDao: EntryDao
    private lateinit var repository: EntryRepository

    @Before
    fun setup() {
        entryDao = mockk()
        repository = EntryRepository(entryDao)
    }

    @Test
    fun `getAllEntries returns flow of entries from dao`() = runTest {
        // Given
        val entries = listOf(
            Entry(id = 1, title = "Test 1", content = "Content 1", date = Date()),
            Entry(id = 2, title = "Test 2", content = "Content 2", date = Date())
        )
        coEvery { entryDao.getAllEntries() } returns flowOf(entries)

        // When & Then
        repository.getAllEntries().test {
            assertEquals(entries, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `getEntryById returns entry from dao`() = runTest {
        // Given
        val entry = Entry(id = 1, title = "Test", content = "Content", date = Date())
        coEvery { entryDao.getEntryById(1) } returns entry

        // When
        val result = repository.getEntryById(1)

        // Then
        assertEquals(entry, result)
        coVerify { entryDao.getEntryById(1) }
    }

    @Test
    fun `insertEntry delegates to dao and returns inserted id`() = runTest {
        // Given
        val entry = Entry(title = "Test", content = "Content", date = Date())
        val expectedId = 1L
        coEvery { entryDao.insertEntry(entry) } returns expectedId

        // When
        val result = repository.insertEntry(entry)

        // Then
        assertEquals(expectedId, result)
        coVerify { entryDao.insertEntry(entry) }
    }

    @Test
    fun `updateEntry delegates to dao`() = runTest {
        // Given
        val entry = Entry(id = 1, title = "Test", content = "Content", date = Date())
        coEvery { entryDao.updateEntry(entry) } returns Unit

        // When
        repository.updateEntry(entry)

        // Then
        coVerify { entryDao.updateEntry(entry) }
    }

    @Test
    fun `deleteEntry delegates to dao`() = runTest {
        // Given
        val entry = Entry(id = 1, title = "Test", content = "Content", date = Date())
        coEvery { entryDao.deleteEntry(entry) } returns Unit

        // When
        repository.deleteEntry(entry)

        // Then
        coVerify { entryDao.deleteEntry(entry) }
    }
}
