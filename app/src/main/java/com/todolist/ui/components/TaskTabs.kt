package com.todolist.ui.components
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.todolist.model.Task
import com.todolist.model.TaskTag

@Composable
fun TaskTagDropdown(
    selectedTag: String,
    onTagSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) } // State to manage the dropdown expansion
    val tags = listOf("All") + TaskTag.values().map { it.displayName } // List of tags including "All"

    Box(modifier = modifier) {
        Button(
            onClick = { expanded = !expanded }, // Toggle the dropdown expansion
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
        ) {
            Text(text = selectedTag, color = Color.White) // Display the selected tag
        }
        DropdownMenu(
            expanded = expanded, // Show the dropdown menu when expanded is true
            onDismissRequest = { expanded = false }, // Close the dropdown menu when dismissed
            modifier = Modifier.background(Color.White)
        ) {
            tags.forEach { tag ->
                DropdownMenuItem(
                    text = { Text(tag) },
                    onClick = {
                        onTagSelected(tag) // Notify the selected tag
                        expanded = false // Close the dropdown menu
                    }
                )
            }
        }
    }
}

fun filterTasksByTag(tasks: List<Task>, tag: String): List<Task> {
    // Filter tasks by tag, if "All" is selected, return all tasks
    return if (tag == "All") tasks else tasks.filter { it.tags.contains(tag) }
}