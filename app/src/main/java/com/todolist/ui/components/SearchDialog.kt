package com.todolist.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.todolist.model.Task

@Composable
fun SearchDialog(
    showDialog: MutableState<Boolean>,
    tasks: List<Task>,
    updateSearchResults: (List<Task>) -> Unit
) {
    var searchText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { showDialog.value = false },
        title = { Text("Search Tasks") },
        text = {
            Column {
                BasicTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(onClick = {
                    val results = tasks.filter {
                        it.title.contains(searchText, ignoreCase = true) ||
                                it.description.contains(searchText, ignoreCase = true) ||
                                it.date.contains(searchText) ||
                                it.tags.joinToString().contains(searchText, ignoreCase = true)
                    }
                    updateSearchResults(results)
                }) {
                    Text("Search")
                }
            }
        },
        confirmButton = {
            Button(onClick = { showDialog.value = false }) {
                Text("Close")
            }
        }
    )
}
