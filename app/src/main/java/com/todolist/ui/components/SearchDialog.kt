package com.todolist.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.todolist.model.Task

@Composable
fun SearchBar(isSearching: MutableState<Boolean>, tasks: List<Task>, searchResults: MutableList<Task>) {
    var searchText by remember { mutableStateOf("") }

    TextField(
        value = searchText,
        onValueChange = { newText ->
            searchText = newText
            if (newText.isEmpty()) {
                isSearching.value = false
                searchResults.clear()
            } else {
                isSearching.value = true
                searchResults.clear()
                searchResults.addAll(tasks.filter {
                    it.title.contains(newText, ignoreCase = true) ||
                            it.description.contains(newText, ignoreCase = true) ||
                            it.date.contains(newText) ||
                            it.tags.joinToString().contains(newText, ignoreCase = true)
                })
            }
        },
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Type to search tasks...") }
    )
}
