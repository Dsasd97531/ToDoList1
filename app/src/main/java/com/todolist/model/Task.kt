package com.todolist.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var title: String,
    var description: String,
    var date: String,
    var tags: List<String>,
    var priority: String,
    var isStarred: Boolean
)