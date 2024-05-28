package com.todolist.data

import androidx.room.TypeConverter

class Converters {
    // Converts string to a list of strings
    @TypeConverter
    fun fromString(value: String): List<String> {
        return value.split(",").map { it.trim() } // Splits the string by comma and trims each item
    }

    // Converts a list of strings to a comma separated string
    @TypeConverter
    fun fromList(list: List<String>): String {
        return list.joinToString(",") // Joins the list into a single string separated by commas
    }
}