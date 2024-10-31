package com.example.mydiary.data.repository

import com.example.mydiary.data.dao.EntryImageDao
import com.example.mydiary.data.entities.EntryImage
import kotlinx.coroutines.flow.Flow

class EntryImageRepository(private val entryImageDao: EntryImageDao) {

    fun getEntryImagesById(id: Long): Flow<List<EntryImage>> = entryImageDao.getImagesForEntry(id)

    fun getImagePathsForEntry(id: Long): Flow<List<String>> = entryImageDao.getImagePathsForEntry(id)

    suspend fun insertEntryImage(entryImage: EntryImage) = entryImageDao.insert(entryImage)

    suspend fun deleteEntryImage(entryImage: EntryImage) = entryImageDao.delete(entryImage)

    suspend fun deleteAllImagesForEntry(entryId: Long) = entryImageDao.deleteAllForEntry(entryId)

    suspend fun deleteEntryImageByUri(uri: String) = entryImageDao.deleteByUri(uri)
}
