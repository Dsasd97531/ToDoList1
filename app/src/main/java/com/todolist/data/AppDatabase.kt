package com.todolist.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.todolist.model.Task

// Define a Room database with
@Database(entities = [Task::class], version = 1)
@TypeConverters(Converters::class) // Add type converters if necessary
abstract class AppDatabase : RoomDatabase() {
    // Abstract method to get the DAO (Data Access Object) for task entity
    abstract fun taskDao(): TaskDao
}