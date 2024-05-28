package com.todolist.data

import com.todolist.model.Task

class TaskRepository(private val taskDao: TaskDao) {

    // Retrieves all tasks from the DAO
    suspend fun getAllTasks(): List<Task> {
        return taskDao.getAllTasks()
    }

    // Inserts a new task using the DAO
    suspend fun insertTask(task: Task) {
        taskDao.insert(task)
    }

    // Updates an existing task using the DAO
    suspend fun updateTask(task: Task) {
        taskDao.update(task)
    }

    // Deletes a task using the DAO
    suspend fun deleteTask(task: Task) {
        taskDao.delete(task)
    }

    // Retrieves tasks sorted by priority, either ascending or descending
    fun getTasksSortedByPriority(ascending: Boolean): List<Task> {
        return if (ascending) {
            taskDao.getTasksSortedByPriorityAsc()
        } else {
            taskDao.getTasksSortedByPriorityDesc()
        }
    }

    // Retrieves tasks sorted by date, either ascending or descending
    fun getTasksSortedByDate(ascending: Boolean): List<Task> {
        return if (ascending) {
            taskDao.getTasksSortedByDateAsc()
        } else {
            taskDao.getTasksSortedByDateDesc()
        }
    }
}
