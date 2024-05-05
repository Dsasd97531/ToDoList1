package com.todolist.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.todolist.model.Task
import com.todolist.ui.components.SortDialog
import com.todolist.ui.components.TaskDialog
import com.todolist.ui.theme.ToDoListTheme
import kotlinx.coroutines.launch
import com.todolist.data.TaskRepository
import com.todolist.util.priorityToFloat
import kotlinx.coroutines.MainScope


class MainActivity : ComponentActivity() {
    private lateinit var taskRepository: TaskRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        taskRepository = TaskRepository(applicationContext)  // Инициализация репозитория с контекстом

        setContent {
            ToDoListTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    ToDoList()
                }
            }
        }
    }

    @Composable
    fun ToDoList() {
        val tasks = remember { mutableStateListOf<Task>() }
        val scrollState = rememberScrollState()
        val showAddDialog = remember { mutableStateOf(false) }
        val showSortDialog = remember { mutableStateOf(false) }
        val sortOption = remember { mutableStateOf("Priority") }
        val sortAscending = remember { mutableStateOf(true) }

        // Load tasks initially
        LaunchedEffect(Unit) {
            tasks.addAll(taskRepository.loadTasks())
        }

        // Respond to sorting changes
        LaunchedEffect(sortOption.value, sortAscending.value) {
            if (sortAscending.value) {
                when (sortOption.value) {
                    "Priority" -> tasks.sortBy { priorityToFloat(it.priority) }
                    "Date" -> tasks.sortBy { it.date }
                }
            } else {
                when (sortOption.value) {
                    "Priority" -> tasks.sortByDescending { priorityToFloat(it.priority) }
                    "Date" -> tasks.sortByDescending { it.date }
                }
            }
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                Button(onClick = { showSortDialog.value = true }) {
                    Text("Sort Tasks")
                }
            },
            bottomBar = {
                Button(onClick = { showAddDialog.value = true }) {
                    Text("Add Task")
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(scrollState)
            ) {
                tasks.forEach { task ->
                    TaskItem(task, tasks)
                }
                if (showAddDialog.value) {
                    TaskDialog(
                        newTaskTitle = remember { mutableStateOf("") },
                        newTaskDescription = remember { mutableStateOf("") },
                        newTaskDate = remember { mutableStateOf("") },
                        newTaskTags = remember { mutableStateOf("") },
                        newTaskPriority = remember { mutableStateOf("Low") },
                        tasks = tasks,
                        taskRepository = taskRepository,
                        showDialog = showAddDialog
                    )
                }
                if (showSortDialog.value) {
                    SortDialog(
                        showDialog = showSortDialog,
                        sortOption = sortOption,
                        sortAscending = sortAscending
                    )
                }
            }
        }
    }
    @Composable
    fun TaskItem(task: Task, tasks: MutableList<Task>) {
        Box(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(
                    modifier = Modifier.weight(1f).padding(end = 8.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text("Title: ${task.title}")
                    Text("Description: ${task.description}")
                    Text("Date: ${task.date}")
                    Text("Tags: ${task.tags.joinToString(", ")}")
                    Text("Priority: ${task.priority}")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    MainScope().launch {
                        tasks.remove(task)
                        taskRepository.saveTasks(tasks)  // Обновление задач через репозиторий
                    }
                }) {
                    Text("Delete")
                }
            }
        }
    }
}
