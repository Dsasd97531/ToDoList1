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

    val repository: TaskRepository

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    init {
        val taskDao = DatabaseProvider.getDatabase(application).taskDao()
        repository = TaskRepository(taskDao)
        loadTasks()
    }

    fun loadTasks() {
        viewModelScope.launch {
            val taskList = withContext(Dispatchers.IO) {
                repository.getAllTasks()
            }
            _tasks.value = taskList
        }
    }

    fun insertTask(task: Task) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.insertTask(task)
            }
            // Добавляем новую задачу к текущему списку
            _tasks.value = _tasks.value + task
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.updateTask(task)
            }
            // Обновляем задачу в текущем списке
            _tasks.value = _tasks.value.map {
                if (it.id == task.id) task else it
            }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.deleteTask(task)
            }
            // Удаляем задачу из текущего списка
            _tasks.value = _tasks.value.filter { it.id != task.id }
        }
    }


    fun sortTasksByPriority(ascending: Boolean) {
        viewModelScope.launch {
            val sortedTasks = withContext(Dispatchers.IO) {
                repository.getTasksSortedByPriority(ascending)
            }
            _tasks.value = sortedTasks
        }
    }

    fun sortTasksByDate(ascending: Boolean) {
        viewModelScope.launch {
            val sortedTasks = withContext(Dispatchers.IO) {
                repository.getTasksSortedByDate(ascending)
            }
            _tasks.value = sortedTasks
        }
    }
}
