package com.todolist.data

import android.content.Context
import com.todolist.model.Task

class TaskRepository(context: Context) {
    private val sharedPreferencesManager = SharedPreferencesManager(context)

    fun saveTasks(tasks: List<Task>) {
        sharedPreferencesManager.saveTasks(tasks)
    }

    fun saveTask(task:Task) {
        sharedPreferencesManager.saveTask(task)
    }

    fun loadTasks(): List<Task> {
        return sharedPreferencesManager.loadTasks()
    }
}
