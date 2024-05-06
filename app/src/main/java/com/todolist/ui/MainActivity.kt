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
import androidx.compose.material3.TextField
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
import com.todolist.ui.components.SearchBar
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
        val allTasks = remember { mutableStateListOf<Task>() } // Основной список задач
        val displayedTasks = remember { mutableStateListOf<Task>() }
        val scrollState = rememberScrollState()
        val showAddDialog = remember { mutableStateOf(false) }
        val showSortDialog = remember { mutableStateOf(false) }
        val showSearchDialog = remember { mutableStateOf(false) }
        val sortOption = remember { mutableStateOf("Priority") }
        val sortAscending = remember { mutableStateOf(true) }
        val isSearching = remember { mutableStateOf(false) }
        val searchQuery = remember { mutableStateOf("") }

        // Load tasks initially
        LaunchedEffect(true) {
            allTasks.addAll(taskRepository.loadTasks())
            displayedTasks.addAll(allTasks) // По умолчанию отображаем все задачи
        }
        // Respond to sorting changes
        LaunchedEffect(sortOption.value, sortAscending.value) {
            if (sortAscending.value) {
                when (sortOption.value) {
                    "Priority" -> allTasks.sortBy { priorityToFloat(it.priority) }
                    "Date" -> allTasks.sortBy { it.date }
                }
            } else {
                when (sortOption.value) {
                    "Priority" -> allTasks.sortByDescending { priorityToFloat(it.priority) }
                    "Date" -> allTasks.sortByDescending { it.date }
                }
            }
            if (!isSearching.value) {
                displayedTasks.clear()
                displayedTasks.addAll(allTasks)
            }
        }

        LaunchedEffect(searchQuery.value) {
            if (searchQuery.value.isEmpty()) {
                displayedTasks.clear()
                displayedTasks.addAll(allTasks) // Если строка поиска пуста, показываем все задачи
            } else {
                val results = allTasks.filter {
                    it.title.contains(searchQuery.value, ignoreCase = true) ||
                            it.description.contains(searchQuery.value, ignoreCase = true) ||
                            it.date.contains(searchQuery.value) ||
                            it.tags.joinToString().contains(searchQuery.value, ignoreCase = true)
                }
                displayedTasks.clear()
                displayedTasks.addAll(results)
            }
        }

        LaunchedEffect(allTasks.size) {
            if (!isSearching.value) {
                displayedTasks.clear()
                displayedTasks.addAll(allTasks)
            }
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),

            topBar = {
                Row {  // Горизонтальное размещение кнопок
                    Button(onClick = { showSortDialog.value = true }) {
                        Text("Sort Tasks")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        showSearchDialog.value = !showSearchDialog.value
                        if (!showSearchDialog.value) {
                            searchQuery.value = "" // Очищаем поиск при закрытии
                            isSearching.value = false
                        }
                    }) {
                        Text(if (showSearchDialog.value) "Close Search" else "Search Tasks")
                    }
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
                if (showSearchDialog.value) {
                    TextField(
                        value = searchQuery.value,
                        onValueChange = { newText ->
                            searchQuery.value = newText
                            isSearching.value = newText.isNotEmpty()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Type to search tasks...") }
                    )
                }
                displayedTasks.forEach { task ->
                    TaskItem(task, allTasks)
                }
                if (showAddDialog.value) {
                    TaskDialog(
                        newTaskTitle = remember { mutableStateOf("") },
                        newTaskDescription = remember { mutableStateOf("") },
                        newTaskDate = remember { mutableStateOf("") },
                        newTaskTags = remember { mutableStateOf("") },
                        newTaskPriority = remember { mutableStateOf("Low") },
                        allTasks = allTasks,
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
