package com.todolist.data

import android.content.Context
import android.content.SharedPreferences
import com.todolist.model.Task

class SharedPreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("ToDoPrefs", Context.MODE_PRIVATE)

    fun saveTasks(tasks: List<Task>) {
        val editor = prefs.edit()
        val tasksAsStrings = tasks.map { task ->
            "${task.id}|${task.title}|${task.description}|${task.date}|${task.tags.joinToString(",")}|${task.priority}"
        }
        editor.putStringSet("tasks", tasksAsStrings.toSet())
        editor.apply()
    }

    fun loadTasks(): List<Task> {
        val taskSet = prefs.getStringSet("tasks", emptySet())
        return taskSet?.map {
            val parts = it.split("|")
            Task(
                id = parts[0].toInt(),
                title = parts[1],
                description = parts[2],
                date = parts[3],
                tags = parts[4].split(",").filter { tag -> tag.isNotEmpty() },
                priority = parts[5]
            )
        } ?: emptyList()
    }
}
