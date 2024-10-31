package com.example.mydiary.data.repository

import com.example.mydiary.data.dao.EntryImageDao
import com.example.mydiary.data.entities.EntryImage

class EntryImageRepository(private val entryImageDao: EntryImageDao) {

    fun getEntryImagesById(id: Long): List<EntryImage> = entryImageDao.getImagesForEntry(id)

    suspend fun insertEntryImage(entryImage: EntryImage) = entryImageDao.insert(entryImage)

    suspend fun deleteEntryImage(entryImage: EntryImage) = entryImageDao.delete(entryImage)

    suspend fun deleteAllImagesForEntry(entryId: Long) = entryImageDao.deleteAllForEntry(entryId)
}
