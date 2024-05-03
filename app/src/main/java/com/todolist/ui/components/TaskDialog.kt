package com.todolist.ui.components

import android.app.DatePickerDialog
import android.content.Context
import android.widget.DatePicker
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import com.todolist.model.Task
import com.todolist.ui.floatToPriority
import com.todolist.ui.priorityToFloat
import com.todolist.ui.saveTasksToPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

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
