package com.todolist.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.todolist.model.Task
import com.todolist.model.TaskTag
import com.todolist.ui.components.*
import com.todolist.ui.theme.ToDoListTheme
import com.todolist.viewmodel.TaskViewModel
import com.todolist.util.formatDate
import com.todolist.util.intToPriority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


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
    val tasks by taskViewModel.tasks.collectAsState(emptyList())
    val scrollState = rememberScrollState()
    val showAddDialog = remember { mutableStateOf(false) }
    val editDialogStates = remember { mutableStateMapOf<Task, MutableState<Boolean>>() }
    val showSortDialog = remember { mutableStateOf(false) }
    val showSearchDialog = remember { mutableStateOf(false) }
    val searchQuery = remember { mutableStateOf("") }
    val sortOption = remember { mutableStateOf("Priority") }
    val sortAscending = remember { mutableStateOf(true) }
    val selectedTag = remember { mutableStateOf("All") }
    val showStarredTasksOnly = remember { mutableStateOf(false) }

    var filteredTasks = filterTasksByTag(
        filterTasks(
            tasks,
            searchQuery.value,
            showStarredTasksOnly.value
        ),
        selectedTag.value
    )

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
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TaskTagDropdown(
                        selectedTag = selectedTag.value,
                        onTagSelected = { selectedTag.value = it },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            showStarredTasksOnly.value = !showStarredTasksOnly.value
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    ) {
                        Text(
                            text = if (showStarredTasksOnly.value) "Show All Tasks" else "Show Starred Tasks",
                            color = Color.White
                        )
                    }
                }
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
                .verticalScroll(scrollState)
        ) {
            if (filteredTasks.isEmpty() && searchQuery.value.isNotEmpty()) {
                Text("No tasks found", modifier = Modifier.padding(16.dp))
            } else {
                filteredTasks.forEach { task ->
                    val editState = editDialogStates.getOrPut(task) { mutableStateOf(false) }
                    key(task.id) { // Ensure proper recomposition with key
                        TaskItem(task = task, showDialog = editState, taskViewModel = taskViewModel)
                    }
                }
            }
            if (showAddDialog.value) {
                TaskDialog(
                    newTaskTitle = remember { mutableStateOf("") },
                    newTaskDescription = remember { mutableStateOf("") },
                    newTaskDate = remember { mutableStateOf<Long?>(null) },
                    newTaskTags = remember { mutableStateOf(TaskTag.Work) },
                    newTaskPriority = remember { mutableStateOf("Low") },
                    initialIsDone = false,
                    initialIsStarred = false,
                    showDialog = showAddDialog,
                    taskViewModel = taskViewModel
                )
            }
            if (showSortDialog.value) {
                SortDialog(
                    showDialog = showSortDialog,
                    sortOption = sortOption,
                    sortAscending = sortAscending,
                    onSortOptionSelected = { option, ascending ->
                        when (option) {
                            "Priority" -> taskViewModel.sortTasksByPriority(ascending)
                            "Date" -> taskViewModel.sortTasksByDate(ascending)
                        }
                    }
                )
            }
        }
    }
}


@Composable
fun TaskItem(
    task: Task,
    showDialog: MutableState<Boolean>,
    taskViewModel: TaskViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    val isStarred = remember { mutableStateOf(task.isStarred) }
    val isDone = remember { mutableStateOf(task.isDone) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 8.dp)
            .border(
                2.dp,
                Color(android.graphics.Color.parseColor("#40739e")),
                shape = MaterialTheme.shapes.medium
            ),
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
                Text("Date: ${formatDate(task.date)}")
                Text("Tags: ${task.tags.joinToString(", ")}")
                Text("Priority: ${intToPriority(task.priority)}")
                Text("IsDone: ${task.isDone}")
            }

            Column(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxHeight()
                    .align(Alignment.CenterVertically)
            ) {
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        isStarred.value = !isStarred.value
                        task.isStarred = isStarred.value
                        coroutineScope.launch {
                            taskViewModel.updateTask(task)
                        }
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Is it your priority task?",
                        tint = if (isStarred.value) Color.Yellow.copy(alpha = 0.8f) else LocalContentColor.current
                    )
                }
                IconButton(
                    onClick = { showDialog.value = true },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit Button",
//                        tint = if (isStarred.value) Color.Yellow.copy(alpha = 0.8f) else LocalContentColor.current
                    )
                }
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            taskViewModel.deleteTask(task)
                        }
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete Button",
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }

        if (showDialog.value) {
            EditDialog(
                task = task,
                showDialog = showDialog,
                newTaskTitle = remember { mutableStateOf(task.title) },
                newTaskDescription = remember { mutableStateOf(task.description) },
                newTaskDate = remember { mutableStateOf(task.date) },
                newTaskTags = remember { mutableStateOf(TaskTag.fromDisplayName(task.tags.first())) },
                newTaskPriority = remember { mutableStateOf(intToPriority(task.priority)) },
                taskRepository = taskViewModel.repository,
                taskViewModel = taskViewModel,
                initialIsStarred = task.isStarred,
                initialIsDone = task.isDone
            )
        }
    }
}
