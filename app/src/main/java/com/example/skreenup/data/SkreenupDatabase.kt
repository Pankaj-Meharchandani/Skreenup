package com.example.skreenup.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Project::class, Preset::class], version = 1, exportSchema = false)
abstract class SkreenupDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun presetDao(): PresetDao

    companion object {
        @Volatile
        private var INSTANCE: SkreenupDatabase? = null

        fun getDatabase(context: Context): SkreenupDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SkreenupDatabase::class.java,
                    "skreenup_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
