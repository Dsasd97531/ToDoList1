package com.todolist.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.TextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.todolist.model.Task

@Composable
fun SearchDialog(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = searchQuery,
        onValueChange = onSearchQueryChanged,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text("Type to search tasks...") }
    )
}


fun filterTasks(tasks: List<Task>, searchQuery: String, showStarredTasksOnly: Boolean): List<Task> {
    val filteredBySearch = if (searchQuery.isEmpty()) tasks else tasks.filter {
        it.title.contains(searchQuery, ignoreCase = true) ||
                it.description.contains(searchQuery, ignoreCase = true)
    }
    return if (showStarredTasksOnly) {
        filteredBySearch.filter { it.isStarred }
    } else {
        filteredBySearch
    }
}