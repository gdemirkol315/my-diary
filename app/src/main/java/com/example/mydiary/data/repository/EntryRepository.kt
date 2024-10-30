package com.example.mydiary.data.repository

import com.example.mydiary.data.dao.EntryDao
import com.example.mydiary.data.entities.Entry
import kotlinx.coroutines.flow.Flow

class EntryRepository(private val entryDao: EntryDao) {
    fun getAllEntries(): Flow<List<Entry>> = entryDao.getAllEntries()

    suspend fun getEntryById(id: Long): Entry? = entryDao.getEntryById(id)

    suspend fun insertEntry(entry: Entry): Long = entryDao.insertEntry(entry)

    suspend fun updateEntry(entry: Entry) = entryDao.updateEntry(entry)

    suspend fun deleteEntry(entry: Entry) = entryDao.deleteEntry(entry)
}
