package com.todolist.ui

import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.DatePicker
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.todolist.ui.theme.ToDoListTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ToDoListTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ToDoList()

                }
            }
        }
    }
}

@Composable
fun ToDoList() {
    val context = LocalContext.current
    var tasks = remember { mutableStateListOf<Task>() }
    val scrollState = rememberScrollState()
    var showAddDialog = remember { mutableStateOf(false) }
    var showSortDialog = remember { mutableStateOf(false) }
    var sortOption = remember { mutableStateOf("Priority") }
    var sortAscending = remember { mutableStateOf(true) }

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

    LaunchedEffect(Unit) {
        tasks.addAll(loadTasksFromPreferences(context))
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
                BoxWithTextAndButton(task, tasks, context, rememberCoroutineScope())
            }
            if (showAddDialog.value) {
                TaskDialog(
                    newTaskTitle = remember { mutableStateOf("") },
                    newTaskDescription = remember { mutableStateOf("") },
                    newTaskDate = remember { mutableStateOf("") },
                    newTaskTags = remember { mutableStateOf("") },
                    newTaskPriority = remember { mutableStateOf("Low") },
                    tasks = tasks,
                    context = context,
                    coroutineScope = rememberCoroutineScope(),
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
fun TaskDialog(
    newTaskTitle: MutableState<String>,
    newTaskDescription: MutableState<String>,
    newTaskDate: MutableState<String>,
    newTaskTags: MutableState<String>,
    newTaskPriority: MutableState<String>,
    tasks: MutableList<Task>,
    context: Context,
    coroutineScope: CoroutineScope,
    showDialog: MutableState<Boolean>
) {
    AlertDialog(
        onDismissRequest = { showDialog.value = false },
        title = { Text("Add New Task") },
        text = {
            Column {
                TextField(
                    value = newTaskTitle.value,
                    onValueChange = { newTaskTitle.value = it },
                    label = { Text("Task Title") }
                )
                TextField(
                    value = newTaskDescription.value,
                    onValueChange = { newTaskDescription.value = it },
                    label = { Text("Task Description") }
                )

                Button(onClick = { showDatePicker(context) { date -> newTaskDate.value = date } }) {
                    Text("Select Date")
                }

                TextField(
                    value = newTaskTags.value,
                    onValueChange = { newTaskTags.value = it },
                    label = { Text("Tags (comma-separated)") }
                )

                Slider(
                    value = priorityToFloat(newTaskPriority.value),
                    onValueChange = { value -> newTaskPriority.value = floatToPriority(value) },
                    valueRange = 0f..4f,
                    steps = 4,
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Priority: ${newTaskPriority.value}")
            }
        },
        confirmButton = {
            Button(onClick = {
                if (newTaskTitle.value.isNotEmpty() &&
                    newTaskDescription.value.isNotEmpty() &&
                    newTaskDate.value.isNotEmpty()) {
                    val newTask = Task(
                        title = newTaskTitle.value,
                        description = newTaskDescription.value,
                        date = newTaskDate.value,
                        tags = newTaskTags.value.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                        priority = newTaskPriority.value
                    )

                    coroutineScope.launch(Dispatchers.IO) {
                        tasks.add(newTask)
                        saveTasksToPreferences(context, tasks)
                    }
                    newTaskTitle.value = ""
                    newTaskDescription.value = ""
                    newTaskDate.value = ""
                    newTaskTags.value = ""
                    newTaskPriority.value = "Low"
                    showDialog.value = false
                }
            }) {
                Text("OK")
            }
        }
    )
}

fun priorityToFloat(priority: String): Float {
    return when (priority) {
        "Medium" -> 1f
        "Important" -> 2f
        "Very Important" -> 3f
        "Urgent" -> 4f
        else -> 0f
    }
}
fun floatToPriority(value: Float): String {
    return when (value.toInt()) {
        1 -> "Medium"
        2 -> "Important"
        3 -> "Very Important"
        4 -> "Urgent"
        else -> "Low"
    }
}

fun showDatePicker(context: Context, onDateSelected: (String) -> Unit) {
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, day: Int ->
            onDateSelected("$year-${month + 1}-$day")
        },
        year, month, day
    )
    datePickerDialog.show()
}


@Composable
fun BoxWithTextAndButton(task: Task, tasks: MutableList<Task>, context: Context, coroutineScope: CoroutineScope) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text("Title: ${task.title}")
                Text("Description: ${task.description}")
                Text("Date of the end: ${task.date}")
                Text("Tags: ${task.tags.joinToString(", ")}")
                Text("Priority: ${task.priority}")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { /* Здесь будет функциональность редактирования */ }) {
                Text("Edit")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                coroutineScope.launch {
                    tasks.remove(task)
                    saveTasksToPreferences(context, tasks) // Убедитесь, что у вас есть функция сохранения изменений
                }
            }) {
                Text("Delete")
            }
        }
    }
}
fun getSharedPreferences(context: Context): SharedPreferences {
    return context.getSharedPreferences("ToDoPrefs", Context.MODE_PRIVATE)
}

fun saveTasksToPreferences(context: Context, tasks: List<Task>) {
    val prefs = getSharedPreferences(context)
    val editor = prefs.edit()

    val tasksAsStrings = tasks.map { "${it.id}|${it.title}|${it.description}|${it.date}|${it.tags.joinToString(",")}|${it.priority}" }
    editor.putStringSet("tasks", tasksAsStrings.toSet())
    editor.apply()
}

fun loadTasksFromPreferences(context: Context): List<Task> {
    val prefs = getSharedPreferences(context)
    val taskSet = prefs.getStringSet("tasks", emptySet())

    return taskSet?.map {
        val parts = it.split("|")
        val id = parts[0].toInt()
        val tags = parts[4].split(",").filter { tag -> tag.isNotEmpty() }
        Task(
            id = id,
            title = parts[1],
            description = parts[2],
            date = parts[3],
            tags = tags,
            priority = parts[5]
        )
    } ?: emptyList()
}

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val date: String,
    val tags: List<String>,
    val priority: String
)

@Composable
fun SortDialog(
    showDialog: MutableState<Boolean>,
    sortOption: MutableState<String>,
    sortAscending: MutableState<Boolean>
) {
    val options = listOf("Priority", "Date") // Оставляем только необходимые опции
    AlertDialog(
        onDismissRequest = { showDialog.value = false },
        title = { Text("Sort Tasks") },
        text = {
            Column {
                options.forEach { option ->
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = {
                                sortOption.value = option
                                sortAscending.value = true
                                showDialog.value = false
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("$option Ascending")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                sortOption.value = option
                                sortAscending.value = false
                                showDialog.value = false
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("$option Descending")
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { showDialog.value = false }) {
                Text("Close")
            }
        }
    )
}