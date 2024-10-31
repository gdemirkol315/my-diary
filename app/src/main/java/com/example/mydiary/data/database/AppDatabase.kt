package com.example.mydiary.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mydiary.data.converter.DateConverter
import com.example.mydiary.data.dao.EntryDao
import com.example.mydiary.data.dao.EntryImageDao
import com.example.mydiary.data.entities.Entry
import com.example.mydiary.data.entities.EntryImage

@Database(entities = [Entry::class, EntryImage::class], version = 2, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun entryDao(): EntryDao
    abstract fun entryImageDao(): EntryImageDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "diary_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
