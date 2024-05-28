package com.todolist.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.todolist.data.DatabaseProvider
import com.todolist.data.TaskRepository
import com.todolist.model.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    // Repository to interact with the data source
    val repository: TaskRepository

    // StateFlow to hold the list of tasks
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    init {
        // Initialize the DAO and repository
        val taskDao = DatabaseProvider.getDatabase(application).taskDao()
        repository = TaskRepository(taskDao)
        // Load tasks when the ViewModel is created
        loadTasks()
    }

    // Load all tasks from the repository
    fun loadTasks() {
        viewModelScope.launch {
            // Fetch tasks in an IO coroutine
            val taskList = withContext(Dispatchers.IO) {
                repository.getAllTasks()
            }
            // Update the StateFlow with the new list of tasks
            _tasks.value = taskList
        }
    }

    // Insert a new task into the repository
    fun insertTask(task: Task) {
        viewModelScope.launch {
            // Insert task in an IO coroutine
            withContext(Dispatchers.IO) {
                repository.insertTask(task)
            }
            // Reload the tasks after insertion
            loadTasks()
        }
    }

    // Update an existing task in the repository
    fun updateTask(task: Task) {
        viewModelScope.launch {
            // Update task in an IO coroutine
            withContext(Dispatchers.IO) {
                repository.updateTask(task)
            }
            // Update the task list with the modified task
            _tasks.value = _tasks.value.map {
                if (it.id == task.id) task else it
            }
        }
    }

    // Delete a task from the repository
    fun deleteTask(task: Task) {
        viewModelScope.launch {
            // Delete task in an IO coroutine
            withContext(Dispatchers.IO) {
                repository.deleteTask(task)
            }
            // Update the task list by removing the deleted task
            _tasks.value = _tasks.value.filter { it.id != task.id }
        }
    }

    // Sort tasks by priority
    fun sortTasksByPriority(ascending: Boolean) {
        viewModelScope.launch {
            // Fetch sorted tasks in an IO coroutine
            val sortedTasks = withContext(Dispatchers.IO) {
                repository.getTasksSortedByPriority(ascending)
            }
            // Update the StateFlow with the sorted list of tasks
            _tasks.value = sortedTasks
        }
    }

    // Sort tasks by date
    fun sortTasksByDate(ascending: Boolean) {
        viewModelScope.launch {
            // Fetch sorted tasks in an IO coroutine
            val sortedTasks = withContext(Dispatchers.IO) {
                repository.getTasksSortedByDate(ascending)
            }
            // Update the StateFlow with the sorted list of tasks
            _tasks.value = sortedTasks
        }
    }
}