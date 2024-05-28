package com.todolist.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.TextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.todolist.model.Task

@Composable
fun SearchDialog(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // TextField for user to input search query
    TextField(
        value = searchQuery,
        onValueChange = onSearchQueryChanged, // Updates the search query state
        modifier = modifier.fillMaxWidth(), // Fills the maximum width available
        placeholder = { Text("Type to search tasks...") } // Placeholder text
    )
}

fun filterTasks(
    tasks: List<Task>,
    searchQuery: String,
    showStarredTasksOnly: Boolean,
    showCompletedTasksOnly: Boolean
): List<Task> {
    // Filter tasks based on the search query (case insensitive)
    val filteredBySearch = if (searchQuery.isEmpty()) tasks else tasks.filter {
        it.title.contains(searchQuery, ignoreCase = true) ||
                it.description.contains(searchQuery, ignoreCase = true)
    }

    // Further filter tasks to show only starred tasks if required
    val filteredByStarred = if (showStarredTasksOnly) {
        filteredBySearch.filter { it.isStarred }
    } else {
        filteredBySearch
    }

    // Finally filter tasks to show only completed tasks if required
    return if (showCompletedTasksOnly) {
        filteredByStarred.filter { it.isDone }
    } else {
        filteredByStarred
    }
}