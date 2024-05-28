package com.todolist.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SortDialog(
    showDialog: MutableState<Boolean>,
    sortOption: MutableState<String>,
    sortAscending: MutableState<Boolean>,
    onSortOptionSelected: (String, Boolean) -> Unit
) {
    // List of available sort options
    val options = listOf("Priority", "Date")

    AlertDialog(
        onDismissRequest = { showDialog.value = false }, // Close dialog on dismiss request
        title = { Text("Sort Tasks") }, // Title of the dialog
        text = {
            Column {
                options.forEach { option ->
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = {
                                sortOption.value = option // Set selected sort option
                                sortAscending.value = true // Set sort order to ascending
                                showDialog.value = false // Close the dialog
                                onSortOptionSelected(option, true) // Notify the selected option and order
                            }
                        ) {
                            Text("$option Ascending")
                        }
                        Spacer(modifier = Modifier.width(8.dp)) // Add space between buttons
                        Button(
                            onClick = {
                                sortOption.value = option // Set selected sort option
                                sortAscending.value = false // Set sort order to descending
                                showDialog.value = false // Close the dialog
                                onSortOptionSelected(option, false) // Notify the selected option and order
                            }
                        ) {
                            Text("$option Descending")
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { showDialog.value = false }) {
                Text("Close") // Button to close the dialog
            }
        }
    )
}