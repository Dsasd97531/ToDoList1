package com.todolist.model

enum class TaskTag(val displayName: String) {
    Work("Work"),
    Family("Family"),
    Health("Health"),
    Education("Education");

    companion object {
        fun fromDisplayName(displayName: String): TaskTag {
            return values().find { it.displayName == displayName } ?: Work // Возвращает 'Work' как значение по умолчанию
        }
    }
}