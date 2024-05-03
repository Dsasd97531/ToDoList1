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
    sortAscending: MutableState<Boolean>
) {
    val options = listOf("Priority", "Date") // Оставляем только необходимые опции
    AlertDialog(
        onDismissRequest = { showDialog.value = false },
        title = { Text("Sort Tasks") },
        text = {
            Column {
                options.forEach { option ->
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = {
                                sortOption.value = option
                                sortAscending.value = true
                                showDialog.value = false
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("$option Ascending")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                sortOption.value = option
                                sortAscending.value = false
                                showDialog.value = false
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("$option Descending")
                        }
                    }
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
