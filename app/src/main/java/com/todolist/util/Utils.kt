package com.todolist.util

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.DatePicker
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
// Convert priority string to corresponding float value
fun priorityToFloat(priority: String): Float {
    return when (priority) {
        "Medium" -> 1f
        "Important" -> 2f
        "Very Important" -> 3f
        "Urgent" -> 4f
        else -> 0f  // Low priority
    }
}

// Convert float value to corresponding priority string
fun floatToPriority(value: Float): String {
    return when (value.toInt()) {
        1 -> "Medium"
        2 -> "Important"
        3 -> "Very Important"
        4 -> "Urgent"
        else -> "Low"
    }
}

// Convert priority string to corresponding integer value
fun priorityToInt(priority: String): Int {
    return when (priority) {
        "Medium" -> 1
        "Important" -> 2
        "Very Important" -> 3
        "Urgent" -> 4
        else -> 0  // Low priority
    }
}

// Convert integer value to corresponding priority string
fun intToPriority(value: Int): String {
    return when (value) {
        1 -> "Medium"
        2 -> "Important"
        3 -> "Very Important"
        4 -> "Urgent"
        else -> "Low"
    }
}

// Format a timestamp to a readable date string
fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

// Show a date picker dialog and return the selected date as a string
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

// Show a date and time picker dialog and return the selected date and time as a timestamp
fun showDateTimePicker(context: Context, onDateTimeSelected: (Long) -> Unit) {
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    DatePickerDialog(context, { _, selectedYear, selectedMonth, selectedDay ->
        TimePickerDialog(context, { _, selectedHour, selectedMinute ->
            calendar.set(selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute)
            onDateTimeSelected(calendar.timeInMillis)
        }, hour, minute, true).show()
    }, year, month, day).show()
}
