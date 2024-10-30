package com.example.mydiary.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "entry_image")
data class EntryImage(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val entryId: Int,
    val imagePath: String,
    val timestamp: Long
)
