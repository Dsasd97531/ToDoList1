package com.todolist.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.todolist.model.Task
import com.todolist.ui.components.*
import com.todolist.ui.theme.ToDoListTheme
import com.todolist.data.TaskRepository
import com.todolist.util.priorityToFloat
import kotlinx.coroutines.launch
import kotlinx.coroutines.MainScope

class MainActivity : ComponentActivity() {
    private lateinit var taskRepository: TaskRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        taskRepository = TaskRepository(applicationContext)

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
        val allTasks = remember { mutableStateListOf<Task>() }
        val displayedTasks = remember { mutableStateListOf<Task>() }
        val scrollState = rememberScrollState()
        val showAddDialog = remember { mutableStateOf(false) }
        val showSortDialog = remember { mutableStateOf(false) }
        val showSearchDialog = remember { mutableStateOf(false) }
        val searchQuery = remember { mutableStateOf("") }
        val sortOption = remember { mutableStateOf("Priority") }
        val sortAscending = remember { mutableStateOf(true) }
        val selectedTab = remember { mutableStateOf("All") }

        // Load tasks initially
        LaunchedEffect(true) {
            allTasks.addAll(taskRepository.loadTasks())
            displayedTasks.addAll(allTasks)
        }

        // Respond to sorting and search changes
        LaunchedEffect(sortOption.value, sortAscending.value, searchQuery.value) {
            val sortedTasks = if (sortAscending.value) {
                when (sortOption.value) {
                    "Priority" -> allTasks.sortedBy { priorityToFloat(it.priority) }
                    "Date" -> allTasks.sortedBy { it.date }
                    else -> allTasks
                }
            } else {
                when (sortOption.value) {
                    "Priority" -> allTasks.sortedByDescending { priorityToFloat(it.priority) }
                    "Date" -> allTasks.sortedByDescending { it.date }
                    else -> allTasks
                }
            }
            displayedTasks.clear()
            displayedTasks.addAll(filterTasks(sortedTasks, searchQuery.value))
        }

        LaunchedEffect(selectedTab.value) {
            displayedTasks.clear()
            if (selectedTab.value == "All") {
                displayedTasks.addAll(allTasks)
            } else {
                displayedTasks.addAll(allTasks.filter { task ->
                    task.tags.contains(selectedTab.value)  // Предполагается, что tags содержит displayName
                })
            }
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                Column {
                    Row {
                        Button(onClick = { showSortDialog.value = true }) {
                            Text("Sort Tasks")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = {
                            showSearchDialog.value = !showSearchDialog.value
                        }) {
                            Text(if (showSearchDialog.value) "Close Search" else "Search Tasks")
                        }
                    }
                    // Always show tabs below the buttons
                    TaskTabs(
                        selectedTab = selectedTab.value,
                        onTabSelected = { selectedTab.value = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                    // Conditionally show search dialog
                    if (showSearchDialog.value) {
                        SearchDialog(
                            searchQuery = searchQuery.value,
                            onSearchQueryChanged = { newQuery -> searchQuery.value = newQuery },
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                        )
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
                displayedTasks.forEach { task ->
                    TaskItem(task, allTasks)
                }
                if (showAddDialog.value) {
                    TaskDialog(
                        newTaskTitle = remember { mutableStateOf("") },
                        newTaskDescription = remember { mutableStateOf("") },
                        newTaskDate = remember { mutableStateOf("") },
                        newTaskTags = remember { mutableStateOf(TaskTag.Work) },
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
                        taskRepository.saveTasks(tasks)
                    }
                }) {
                    Text("Delete")
                }
            }
        }
    }
}
