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
        onDismissRequest = { showDialog.value = null },
        title = { Text(text = "Task Details") },
        text = {
            Column {
                Text("Title: ${task.title}")
                Text("Description: ${task.description}")
                Text("Date: ${formatDate(task.date)}")
                Text("Tags: ${task.tags.joinToString(", ")}")
                Text("Priority: ${intToPriority(task.priority)}")
                Text("Is Done: ${task.isDone}")
                Text("Is Starred: ${task.isStarred}")
            }
        },
        confirmButton = {
            Button(onClick = { showDialog.value = null }) {
                Text("Close")
            }
        }
    )
}