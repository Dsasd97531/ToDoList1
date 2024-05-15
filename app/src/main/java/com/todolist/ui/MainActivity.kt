package com.todolist.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.todolist.model.Task
import com.todolist.model.TaskTag
import com.todolist.ui.components.*
import com.todolist.ui.theme.ToDoListTheme
import com.todolist.util.priorityToFloat
import com.todolist.viewmodel.TaskViewModel
import kotlinx.coroutines.launch
import androidx.compose.runtime.livedata.observeAsState


class MainActivity : ComponentActivity() {
    private val taskViewModel: TaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ToDoListTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    ToDoListScreen(taskViewModel)
                }
            }
        }
    }
}

@Composable
fun ToDoListScreen(taskViewModel: TaskViewModel) {
    val tasks by taskViewModel.tasks.observeAsState(emptyList())
    val scrollState = rememberScrollState()
    val showAddDialog = remember { mutableStateOf(false) }
    val editDialogStates = remember { mutableStateMapOf<Task, MutableState<Boolean>>() }
    val showSortDialog = remember { mutableStateOf(false) }
    val showSearchDialog = remember { mutableStateOf(false) }
    val searchQuery = remember { mutableStateOf("") }
    val sortOption = remember { mutableStateOf("Priority") }
    val sortAscending = remember { mutableStateOf(true) }
    val selectedTab = remember { mutableStateOf("All") }

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
                TaskTabs(
                    selectedTab = selectedTab.value,
                    onTabSelected = { selectedTab.value = it },
                    modifier = Modifier.fillMaxWidth()
                )
                if (showSearchDialog.value) {
                    SearchDialog(
                        searchQuery = searchQuery.value,
                        onSearchQueryChanged = { newQuery -> searchQuery.value = newQuery },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
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
                .verticalScroll(scrollState)) {
            tasks.sortedByDescending { it.isStarred }.forEach { task ->
                val editState = editDialogStates.getOrPut(task) { mutableStateOf(false) }
                TaskItem(task = task, tasks = tasks.toMutableList(), showDialog = editState, taskViewModel = taskViewModel)
            }
            if (showAddDialog.value) {
                TaskDialog(
                    newTaskTitle = remember { mutableStateOf("") },
                    newTaskDescription = remember { mutableStateOf("") },
                    newTaskDate = remember { mutableStateOf("") },
                    newTaskTags = remember { mutableStateOf(TaskTag.Work) },
                    newTaskPriority = remember { mutableStateOf("Low") },
                    initialIsStarred = false,
                    allTasks = tasks.toMutableStateList(),
                    taskRepository = taskViewModel.repository,
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
fun TaskItem(task: Task, tasks: MutableList<Task>, showDialog: MutableState<Boolean>, taskViewModel: TaskViewModel) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 10.dp, vertical = 8.dp)
        .border(2.dp, Color(android.graphics.Color.parseColor("#40739e")), shape = MaterialTheme.shapes.medium),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text("Title: ${task.title}")
                Text("Description: ${task.description}")
                Text("Date: ${task.date}")
                Text("Tags: ${task.tags.joinToString(", ")}")
                Text("Priority: ${task.priority}")
            }

            Column(modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxHeight()
                .align(Alignment.CenterVertically)
            ) {
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {},
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Is it your priority task?",
                        tint = if (task.isStarred) Color.Yellow.copy(alpha = 0.8f) else LocalContentColor.current
                    )
                }
                Button(onClick = { showDialog.value = true }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Text(text = "Edit")
                }
                Button(onClick = {
                    taskViewModel.deleteTask(task)
                }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Text("Delete")
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }

        val initialTag = remember(task) { TaskTag.fromDisplayName(task.tags.first()) }
        val newTaskTags = remember { mutableStateOf(initialTag) }
        if (showDialog.value) {
            EditDialog(
                task = task,
                showDialog = showDialog,
                newTaskTitle = remember { mutableStateOf(task.title) },
                newTaskDescription = remember { mutableStateOf(task.description) },
                newTaskDate = remember { mutableStateOf(task.date) },
                newTaskTags = newTaskTags,
                newTaskPriority = remember { mutableStateOf(task.priority) },
                taskRepository = taskViewModel.repository,
                initialIsStarred = task.isStarred
            )
        }
    }
}
