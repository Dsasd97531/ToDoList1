package com.todolist.ui.components

import android.app.DatePickerDialog
import android.content.Context
import android.widget.DatePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.todolist.data.TaskRepository
import com.todolist.model.Task
import com.todolist.util.floatToPriority
import com.todolist.util.priorityToFloat
import com.todolist.util.showDatePicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Calendar

enum class TaskTag(val displayName: String) {
    Work("Work"),
    Family("Family"),
    Health("Health"),
    Education("Education");

    companion object {
        fun fromDisplayName(displayName: String): TaskTag {
            return values().find { it.displayName == displayName } ?: Work // Возвращает 'Work' как значение по умолчанию
        }
    }
}

@Composable
fun TaskDialog(
    newTaskTitle: MutableState<String>,
    newTaskDescription: MutableState<String>,
    newTaskDate: MutableState<String>,
    newTaskTags: MutableState<TaskTag>,
    newTaskPriority: MutableState<String>,
    initialIsStarred: Boolean,
    allTasks: SnapshotStateList<Task>,
    showDialog: MutableState<Boolean>,
    context: Context = LocalContext.current,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    taskRepository: TaskRepository
) {
    var expanded by remember { mutableStateOf(false) }
    var isStarred by remember { mutableStateOf((initialIsStarred)) }

    AlertDialog(
        onDismissRequest = { showDialog.value = false },
        title = {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add New Task")
            IconButton(
                onClick = {
                    isStarred = !isStarred
                },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "Is it your priority task?",
                    tint = if(isStarred) Color.Yellow.copy(alpha = 0.8f) else LocalContentColor.current
                )
            }
        }},
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
                // Button to trigger the Popup for tag selection
                Button(onClick = { expanded = true }) {
                    Text("Select Tag: ${newTaskTags.value.displayName}")
                    Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = "Dropdown Icon")
                }
                // Popup for selecting tags
                if (expanded) {
                    Popup(alignment = Alignment.Center) {
                        Column(modifier = Modifier.background(Color.White).padding(8.dp)) {
                            TaskTag.values().forEach { tag ->
                                TextButton(
                                    onClick = {
                                        newTaskTags.value = tag
                                        expanded = false
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
                if (newTaskTitle.value.isNotEmpty() && newTaskDescription.value.isNotEmpty() && newTaskDate.value.isNotEmpty()) {
                    val newTask = Task(
                        title = newTaskTitle.value,
                        description = newTaskDescription.value,
                        date = newTaskDate.value,
                        tags = listOf(newTaskTags.value.displayName),
                        priority = newTaskPriority.value,
                        isStarred = isStarred
                    )
                    allTasks.add(newTask)
                    coroutineScope.launch {
                        taskRepository.saveTasks(allTasks)
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

