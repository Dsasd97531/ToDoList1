package com.todolist.util

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