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

fun filterTasks(tasks: List<Task>, query: String): List<Task> {
    return if (query.isEmpty()) tasks else tasks.filter {
        it.title.contains(query, ignoreCase = true) ||
                it.description.contains(query, ignoreCase = true) ||
                it.date.contains(query) ||
                it.tags.joinToString().contains(query, ignoreCase = true)
    }
}
