package com.todolist.util

import android.app.DatePickerDialog
import android.content.Context
import android.widget.DatePicker
import java.util.Calendar

fun priorityToFloat(priority: String): Float {
    return when (priority) {
        "Medium" -> 1f
        "Important" -> 2f
        "Very Important" -> 3f
        "Urgent" -> 4f
        else -> 0f
    }
}
fun floatToPriority(value: Float): String {
    return when (value.toInt()) {
        1 -> "Medium"
        2 -> "Important"
        3 -> "Very Important"
        4 -> "Urgent"
        else -> "Low"
    }
}


fun showDatePicker(context: Context, onDateSelected: (String) -> Unit) {
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, day: Int ->
            onDateSelected("$year-${month + 1}-$day")
        },
        year, month, day
    )
    datePickerDialog.show()
}
