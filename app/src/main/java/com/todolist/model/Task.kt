package com.todolist.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var title: String,
    var description: String,
    var date: Long,
    var tags: List<String>,
    var priority: Int,
    var isStarred: Boolean
)