package com.todolist.ui.components

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
import com.todolist.data.TaskRepository
import com.todolist.model.Task
import com.todolist.model.TaskTag
import com.todolist.util.floatToPriority
import com.todolist.util.formatDate
import com.todolist.util.priorityToFloat
import com.todolist.util.priorityToInt
import com.todolist.util.showDateTimePicker
import com.todolist.viewmodel.TaskViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun EditDialog(
    task: Task,
    showDialog: MutableState<Boolean>,
    newTaskTitle: MutableState<String>,
    newTaskDescription: MutableState<String>,
    newTaskDate: MutableState<Long?>,
    newTaskTags: MutableState<TaskTag>,
    initialIsDone: Boolean,
    newTaskPriority: MutableState<String>,
    taskRepository: TaskRepository,
    taskViewModel: TaskViewModel,  // Added TaskViewModel
    initialIsStarred: Boolean,
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) {
    // Local context for accessing resources
    val context = LocalContext.current

    // State to manage the starred status of the task
    var isStarred by remember { mutableStateOf(initialIsStarred) }
    // State to manage the dropdown menu expansion
    var expanded by remember { mutableStateOf(false) }
    // State to manage the done status of the task
    var isDone by remember { mutableStateOf(initialIsDone) }

    AlertDialog(
        onDismissRequest = { showDialog.value = false }, // Close dialog on dismiss request
        title = {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Edit Task")
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
                    onValueChange = { newTaskTitle.value = it }, // Update task title state
                    label = { Text("Task Title") }
                )
                TextField(
                    value = newTaskDescription.value,
                    onValueChange = { newTaskDescription.value = it }, // Update task description state
                    label = { Text("Task Description") }
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
                    onValueChange = { value -> newTaskPriority.value = floatToPriority(value) }, // Update task priority state
                    valueRange = 0f..4f,
                    steps = 4,
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Priority: ${newTaskPriority.value}") // Display current priority
            }
        },
        confirmButton = {
            Button(onClick = {
                // Check if the task details are valid before updating
                if (newTaskTitle.value.isNotEmpty() && newTaskDescription.value.isNotEmpty() && newTaskDate.value != null) {
                    val updatedTask = task.copy(
                        title = newTaskTitle.value,
                        description = newTaskDescription.value,
                        date = newTaskDate.value!!,
                        tags = listOf(newTaskTags.value.displayName),
                        priority = priorityToInt(newTaskPriority.value),
                        isStarred = isStarred,
                        isDone = isDone
                    )
                    coroutineScope.launch {
                        taskRepository.updateTask(updatedTask) // Update the task in the repository
                        taskViewModel.loadTasks() // Reload tasks in the view model
                    }
                    showDialog.value = false // Close the dialog
                }
            }) {
                Text("OK")
            }
        }
    )
}