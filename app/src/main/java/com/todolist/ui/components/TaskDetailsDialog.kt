package com.todolist.ui.components


import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.todolist.model.Task
import com.todolist.util.formatDate
import com.todolist.util.intToPriority


@Composable
fun TaskDetailsDialog(
    task: Task,
    showDialog: MutableState<Task?>
) {
    AlertDialog(
        onDismissRequest = { showDialog.value = null }, // Close the dialog when dismissed
        title = { Text(text = "Task Details") }, // Title of the dialog
        text = {
            Column {
                Text("Title: ${task.title}") // Display the task title
                Text("Description: ${task.description}") // Display the task description
                Text("Date: ${formatDate(task.date)}") // Display the formatted task date
                Text("Tags: ${task.tags.joinToString(", ")}") // Display the task tags
                Text("Priority: ${intToPriority(task.priority)}") // Display the task priority
                Text("Is Done: ${task.isDone}") // Display whether the task is done
                Text("Is Starred: ${task.isStarred}") // Display whether the task is starred
            }
        },
        confirmButton = {
            Button(onClick = { showDialog.value = null }) {
                Text("Close") // Button to close the dialog
            }
        }
    )
}