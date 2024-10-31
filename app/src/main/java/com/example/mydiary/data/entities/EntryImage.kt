package com.example.mydiary.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "entry_image",
    foreignKeys = [
        ForeignKey(
            entity = Entry::class,
            parentColumns = ["id"],
            childColumns = ["entryId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class EntryImage(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val entryId: Long,
    val imagePath: String,
    val timestamp: Long = System.currentTimeMillis()
)