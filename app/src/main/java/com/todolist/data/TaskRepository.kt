package com.todolist.data

import com.todolist.model.Task

class TaskRepository(private val taskDao: TaskDao) {

    suspend fun getAllTasks(): List<Task> {
        return taskDao.getAllTasks()
    }

    suspend fun insertTask(task: Task) {
        taskDao.insert(task)
    }

    suspend fun updateTask(task: Task) {
        taskDao.update(task)
    }

    suspend fun deleteTask(task: Task) {
        taskDao.delete(task)
    }

    fun getTasksSortedByPriority(ascending: Boolean): List<Task> {
        return if (ascending) {
            taskDao.getTasksSortedByPriorityAsc()
        } else {
            taskDao.getTasksSortedByPriorityDesc()
        }
    }

    fun getTasksSortedByDate(ascending: Boolean): List<Task> {
        return if (ascending) {
            taskDao.getTasksSortedByDateAsc()
        } else {
            taskDao.getTasksSortedByDateDesc()
        }
    }
}
