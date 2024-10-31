package com.example.mydiary.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.mydiary.data.entities.EntryImage
import kotlinx.coroutines.flow.Flow

@Dao
interface EntryImageDao {
    @Insert
    suspend fun insert(entryImage: EntryImage)

    @Delete
    suspend fun delete(entryImage: EntryImage)

    @Query("SELECT * FROM entry_image WHERE entryId = :entryId ORDER BY timestamp ASC")
    fun getImagesForEntry(entryId: Long): Flow<List<EntryImage>>

    @Query("SELECT imagePath FROM entry_image WHERE entryId = :entryId ORDER BY timestamp ASC")
    fun getImagePathsForEntry(entryId: Long): Flow<List<String>>

    @Query("DELETE FROM entry_image WHERE entryId = :entryId")
    suspend fun deleteAllForEntry(entryId: Long)

    @Query("DELETE FROM entry_image WHERE imagePath = :uri")
    suspend fun deleteByUri(uri: String)
}