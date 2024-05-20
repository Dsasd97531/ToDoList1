package com.todolist.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.todolist.model.Task
import com.todolist.model.TaskTag
import com.todolist.ui.components.*
import com.todolist.ui.theme.ToDoListTheme
import com.todolist.util.NotificationUtils
import com.todolist.viewmodel.TaskViewModel
import com.todolist.util.formatDate
import com.todolist.util.intToPriority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    private val taskViewModel: TaskViewModel by viewModels()

    private val requestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            // Handle the case where the user denied the permission
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        NotificationUtils.createNotificationChannel(this)
        requestNotificationPermissionIfNeeded()

        setContent {
            ToDoListTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    ToDoListScreen(taskViewModel)
                }
            }
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
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
    val showDetailsDialog = remember { mutableStateOf<Task?>(null) }
    val showSearchDialog = remember { mutableStateOf(false) }
    val searchQuery = remember { mutableStateOf("") }
    val sortOption = remember { mutableStateOf("Priority") }
    val sortAscending = remember { mutableStateOf(true) }
    val selectedTag = remember { mutableStateOf("All") }
    val showStarredTasksOnly = remember { mutableStateOf(false) }
    val showCompletedTasksOnly = remember { mutableStateOf(false) }

    val filteredTasks = filterTasksByTag(
        filterTasks(
            tasks,
            searchQuery.value,
            showStarredTasksOnly.value,
            showCompletedTasksOnly.value
        ),
        selectedTag.value
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Column {
                Row {
                    Button(onClick = { showSortDialog.value = true }) {
                        Text("Sort")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        showSearchDialog.value = !showSearchDialog.value
                    }) {
                        Text(if (showSearchDialog.value) "Close" else "Search")
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
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (showStarredTasksOnly.value) Color.Yellow else Color.Gray
                        )
                    ) {
                        Text(
                            text = "Starred",
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            showCompletedTasksOnly.value = !showCompletedTasksOnly.value
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (showCompletedTasksOnly.value) Color.Green else Color.Gray
                        )
                    ) {
                        Text(
                            text = "Completed",
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
                        TaskItem(
                            task = task,
                            showDialog = editState,
                            taskViewModel = taskViewModel,
                            showDetailsDialog = showDetailsDialog
                        )
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

    showDetailsDialog.value?.let { selectedTask ->
        TaskDetailsDialog(
            task = selectedTask,
            showDialog = showDetailsDialog
        )
    }
}

@Composable
fun TaskItem(
    task: Task,
    showDialog: MutableState<Boolean>,
    showDetailsDialog: MutableState<Task?>,
    taskViewModel: TaskViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    val isStarred = remember { mutableStateOf(task.isStarred) }
    val isDone = remember { mutableStateOf(task.isDone) }
    val showDeleteConfirmationDialog = remember { mutableStateOf(false) }

    if (showDeleteConfirmationDialog.value) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmationDialog.value = false },
            title = { Text(text = "Confirm Delete") },
            text = { Text("Are you sure you want to delete this task?") },
            confirmButton = {
                Button(onClick = {
                    coroutineScope.launch {
                        taskViewModel.deleteTask(task)
                    }
                    showDeleteConfirmationDialog.value = false
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                Button(onClick = { showDeleteConfirmationDialog.value = false }) {
                    Text("No")
                }
            }
        )
    }

    val textDecoration = if (isDone.value) TextDecoration.LineThrough else TextDecoration.None
    val textColor = if (isDone.value) Color.Gray else LocalContentColor.current

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
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        if (!isDone.value) {
                            isStarred.value = !isStarred.value
                            task.isStarred = isStarred.value
                            coroutineScope.launch {
                                taskViewModel.updateTask(task)
                            }
                        }
                    },
                    enabled = !isDone.value
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Star Task",
                        tint = if (isStarred.value) Color.Yellow.copy(alpha = 0.8f) else textColor
                    )
                }
                Checkbox(
                    checked = isDone.value,
                    onCheckedChange = {
                        isDone.value = it
                        task.isDone = it
                        coroutineScope.launch {
                            taskViewModel.updateTask(task)
                        }
                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color.Green,
                        uncheckedColor = Color.Red
                    )
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleLarge,
                    textDecoration = textDecoration,
                    color = textColor
                )
                Text(
                    text = formatDate(task.date),
                    style = MaterialTheme.typography.bodyMedium,
                    textDecoration = textDecoration,
                    color = textColor
                )
                Text(
                    text = intToPriority(task.priority),
                    style = MaterialTheme.typography.bodyMedium,
                    textDecoration = textDecoration,
                    color = textColor
                )
                Text(
                    text = task.tags.joinToString(", "),
                    style = MaterialTheme.typography.bodyMedium,
                    textDecoration = textDecoration,
                    color = textColor
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { if (!isDone.value) showDialog.value = true },
                    enabled = !isDone.value
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit Task",
                        tint = if (isDone.value) Color.Gray else LocalContentColor.current
                    )
                }
                IconButton(
                    onClick = {
                        if (isDone.value) {
                            coroutineScope.launch {
                                taskViewModel.deleteTask(task)
                            }
                        } else {
                            showDeleteConfirmationDialog.value = true
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete Task",
                        tint = LocalContentColor.current
                    )
                }
                IconButton(
                    onClick = { showDetailsDialog.value = task }
                ) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = "Task Details",
                    )
                }
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