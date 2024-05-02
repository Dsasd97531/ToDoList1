package com.todolist

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
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
//                    EditTextComponent("","",context = this)
//                    DatePickerComponent()
                    ToDoList()

                }
            }
        }
    }
}


@Composable
fun ToDoList() {
    val context = LocalContext.current
    var tasks = remember { mutableStateListOf<Triple<String, String, String>>() }
    var showDialog = remember { mutableStateOf(false) }
    var newTaskTitle = remember { mutableStateOf("") }
    var newTaskDescription = remember { mutableStateOf("") }
    var newTaskDate = remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()

    // Load tasks from SharedPreferences at startup
    LaunchedEffect(Unit) {
        tasks.clear()
        tasks.addAll(loadTasksFromPreferences(context))
    }

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        tasks.forEach { (title, description, date) ->
            BoxWithTextAndButton(title, description, date)
        }

        Button(onClick = { showDialog.value = true }) {
            Text("Add Task")
        }

        if (showDialog.value) {
            TaskDialog(
                newTaskTitle,
                newTaskDescription,
                newTaskDate,
                tasks,
                context,
                coroutineScope,
                showDialog
            )
        }
    }
}

@Composable
fun TaskDialog(
    newTaskTitle: MutableState<String>,
    newTaskDescription: MutableState<String>,
    newTaskDate: MutableState<String>,
    tasks: MutableList<Triple<String, String, String>>,
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

                Text("Selected Date: ${newTaskDate.value}")
            }
        },
        confirmButton = {
            Button(onClick = {
                if (newTaskTitle.value.isNotEmpty() &&
                    newTaskDescription.value.isNotEmpty() &&
                    newTaskDate.value.isNotEmpty()) {
                    val newTask = Triple(newTaskTitle.value, newTaskDescription.value, newTaskDate.value)

                    coroutineScope.launch(Dispatchers.IO) {
                        tasks.add(newTask)
                        saveTasksToPreferences(context, tasks)
                    }

                    newTaskTitle.value = ""
                    newTaskDescription.value = ""
                    newTaskDate.value = ""
                    showDialog.value = false
                }
            }) {
                Text("OK")
            }
        }
    )
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
fun BoxWithTextAndButton(text1: String, text2: String, date: String) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(8.dp).height(80.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text("Title: $text1")
                Text("Description: $text2")
                Text("Date: $date")
            }
        }
    }
}
fun getSharedPreferences(context: Context): SharedPreferences {
    return context.getSharedPreferences("ToDoPrefs", Context.MODE_PRIVATE)
}

fun saveTasksToPreferences(context: Context, tasks: List<Triple<String, String, String>>) {
    val prefs = getSharedPreferences(context)
    val editor = prefs.edit()

    val tasksAsStrings = tasks.map { "${it.first}|${it.second}|${it.third}" }
    editor.putStringSet("tasks", tasksAsStrings.toSet())
    editor.apply()
}

fun loadTasksFromPreferences(context: Context): List<Triple<String, String, String>> {
    val prefs = getSharedPreferences(context)
    val taskSet = prefs.getStringSet("tasks", emptySet())

    return taskSet?.map {
        val parts = it.split("|")
        Triple(parts[0], parts[1], parts[2])
    } ?: emptyList()
}