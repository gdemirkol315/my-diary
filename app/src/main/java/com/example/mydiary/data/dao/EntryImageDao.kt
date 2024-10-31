package com.example.mydiary.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.mydiary.data.entities.EntryImage

@Dao
interface EntryImageDao {
    @Insert
    suspend fun insert(entryImage: EntryImage)

    @Delete
    suspend fun delete(entryImage: EntryImage)

    @Query("SELECT * FROM entry_image WHERE entryId = :entryId ORDER BY timestamp ASC")
    fun getImagesForEntry(entryId: Long): List<EntryImage>

    @Query("DELETE FROM entry_image WHERE entryId = :entryId")
    suspend fun deleteAllForEntry(entryId: Long)
}