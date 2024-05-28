package com.todolist.data

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    private var database: AppDatabase? = null

    // Provides the singleton instance of AppDatabase
    fun getDatabase(context: Context): AppDatabase {
        if (database == null) {
            // Initialize the database if it hasn't been created yet
            database = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "tasks_db" // Name of the database file
            ).build()
        }
        return database!!
    }
}