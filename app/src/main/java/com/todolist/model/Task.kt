package com.todolist.model

import androidx.room.Entity
import androidx.room.PrimaryKey


// Define the Task entity with a table name "tasks"
@Entity(tableName = "tasks")
data class Task(
    // Primary key with auto-generated value
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var title: String, // Title of the task
    var description: String, // Description of the task
    var date: Long, // Date associated with the task (in milliseconds)
    var tags: List<String>, // List of tags associated with the task
    var priority: Int, // Priority level of the task
    var isStarred: Boolean, // Indicates if the task is starred
    var isDone: Boolean // Indicates if the task is completed
)