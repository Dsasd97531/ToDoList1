package com.todolist.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.todolist.data.DatabaseProvider
import com.todolist.data.TaskRepository
import com.todolist.model.Task
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TaskViewModel(
    application: Application,
    val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    val mainDispatcher: CoroutineDispatcher = Dispatchers.Main
) : AndroidViewModel(application) {
    val repository: TaskRepository

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    init {
        val taskDao = DatabaseProvider.getDatabase(application).taskDao()
        repository = TaskRepository(taskDao)
        loadTasks()
    }

    fun loadTasks() {
        viewModelScope.launch(mainDispatcher) {
            val taskList = withContext(ioDispatcher) {
                repository.getAllTasks()
            }
            _tasks.value = taskList
        }
    }

    fun insertTask(task: Task) {
        viewModelScope.launch(mainDispatcher) {
            withContext(ioDispatcher) {
                repository.insertTask(task)
            }
            loadTasks() // Reload the tasks after insertion
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch(mainDispatcher) {
            withContext(ioDispatcher) {
                repository.updateTask(task)
            }
            // Update the task list after modification
            _tasks.value = _tasks.value.map {
                if (it.id == task.id) task else it
            }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch(mainDispatcher) {
            withContext(ioDispatcher) {
                repository.deleteTask(task)
            }
            // Update the task list after deletion
            _tasks.value = _tasks.value.filter { it.id != task.id }
        }
    }

    fun sortTasksByPriority(ascending: Boolean) {
        viewModelScope.launch(mainDispatcher) {
            val sortedTasks = withContext(ioDispatcher) {
                repository.getTasksSortedByPriority(ascending)
            }
            _tasks.value = sortedTasks
        }
    }

    fun sortTasksByDate(ascending: Boolean) {
        viewModelScope.launch(mainDispatcher) {
            val sortedTasks = withContext(ioDispatcher) {
                repository.getTasksSortedByDate(ascending)
            }
            _tasks.value = sortedTasks
        }
    }
}
