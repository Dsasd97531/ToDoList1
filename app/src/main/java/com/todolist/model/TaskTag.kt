package com.todolist.model

// Enum class representing different task tags with display names
enum class TaskTag(val displayName: String) {
    Work("Work"),
    Family("Family"),
    Health("Health"),
    Education("Education");

    companion object {
        // Function to get a TaskTag enum value from a display name
        fun fromDisplayName(displayName: String): TaskTag {
            // Returns the matching TaskTag or 'Work' as the default value
            return values().find { it.displayName == displayName } ?: Work
        }
    }
}