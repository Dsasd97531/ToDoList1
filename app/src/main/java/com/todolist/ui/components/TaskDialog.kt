package com.todolist.ui.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.todolist.model.Task
import com.todolist.model.TaskTag
import com.todolist.util.floatToPriority
import com.todolist.util.formatDate
import com.todolist.util.priorityToFloat
import com.todolist.util.priorityToInt
import com.todolist.util.showDateTimePicker
import com.todolist.viewmodel.TaskViewModel
import com.todolist.worker.scheduleTaskReminder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun TaskDialog(
    newTaskTitle: MutableState<String>,
    newTaskDescription: MutableState<String>,
    newTaskDate: MutableState<Long?>,
    newTaskTags: MutableState<TaskTag>,
    newTaskPriority: MutableState<String>,
    initialIsStarred: Boolean,
    initialIsDone: Boolean,
    showDialog: MutableState<Boolean>,
    context: Context = LocalContext.current,
    taskViewModel: TaskViewModel,
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) {
    // Local states for managing UI elements
    var expanded by remember { mutableStateOf(false) }
    var isStarred by remember { mutableStateOf(initialIsStarred) }
    var isDone by remember { mutableStateOf(initialIsDone) }

    AlertDialog(
        onDismissRequest = { showDialog.value = false }, // Close the dialog when dismissed
        title = {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add New Task")
                IconButton(
                    onClick = {
                        isStarred = !isStarred // Toggle the starred status
                    },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Is it your priority task?",
                        tint = if (isStarred) Color.Yellow.copy(alpha = 0.8f) else LocalContentColor.current
                    )
                }
            }
        },
        text = {
            Column {
                TextField(
                    value = newTaskTitle.value,
                    onValueChange = { newTaskTitle.value = it }, // Update the task title
                    label = { Text("Task Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = newTaskDescription.value,
                    onValueChange = { newTaskDescription.value = it }, // Update the task description
                    label = { Text("Task Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(onClick = { showDateTimePicker(context) { timestamp -> newTaskDate.value = timestamp } }) {
                    Text("Select Date and Time")
                }
                newTaskDate.value?.let {
                    Text("Selected Date and Time: ${formatDate(it)}") // Display selected date and time
                }
                Button(onClick = { expanded = true }) {
                    Text("Select Tag: ${newTaskTags.value.displayName}")
                    Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = "Dropdown Icon")
                }
                if (expanded) {
                    // Popup for selecting a tag
                    Popup(alignment = Alignment.Center) {
                        Column(modifier = Modifier.background(Color.White).padding(8.dp)) {
                            TaskTag.values().forEach { tag ->
                                TextButton(
                                    onClick = {
                                        newTaskTags.value = tag // Update selected tag
                                        expanded = false // Close the dropdown
                                    }
                                ) {
                                    Text(tag.displayName)
                                }
                            }
                        }
                    }
                }
                Slider(
                    value = priorityToFloat(newTaskPriority.value),
                    onValueChange = { value -> newTaskPriority.value = floatToPriority(value) }, // Update task priority
                    valueRange = 0f..4f,
                    steps = 4,
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Priority: ${newTaskPriority.value}") // Display current priority
            }
        },
        confirmButton = {
            Button(onClick = {
                // Validate input fields before creating a new task
                if (newTaskTitle.value.isNotEmpty() && newTaskDescription.value.isNotEmpty() && newTaskDate.value != null) {
                    val newTask = Task(
                        title = newTaskTitle.value,
                        description = newTaskDescription.value,
                        date = newTaskDate.value!!,
                        tags = listOf(newTaskTags.value.displayName),
                        priority = priorityToInt(newTaskPriority.value),
                        isStarred = isStarred,
                        isDone = isDone,
                    )
                    coroutineScope.launch {
                        taskViewModel.insertTask(newTask) // Insert the new task using the ViewModel
                    }

                    // Schedule notification for the new task
                    scheduleTaskReminder(context, newTask.title, newTask.id, newTask.date)

                    // Reset input fields and close the dialog
                    newTaskTitle.value = ""
                    newTaskDescription.value = ""
                    newTaskDate.value = null
                    showDialog.value = false
                }
            }) {
                Text("OK")
            }
        }
    )
}
