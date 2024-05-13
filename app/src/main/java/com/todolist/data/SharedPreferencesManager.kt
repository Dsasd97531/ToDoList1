package com.todolist.data

import android.content.Context
import android.content.SharedPreferences
import com.todolist.model.Task

class SharedPreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("ToDoPrefs", Context.MODE_PRIVATE)

    fun saveTasks(tasks: List<Task>) {
        val editor = prefs.edit()
        val tasksAsStrings = tasks.map { task ->
            "${task.id}|${task.title}|${task.description}|${task.date}|${task.tags.joinToString(",")}|${task.priority}|${task.isStarred}"
        }
        editor.putStringSet("tasks", tasksAsStrings.toSet())
        editor.apply()

    }

    fun saveTask(task: Task) {
        val editor = prefs.edit()
        // Загрузка существующих задач
        val existingTasks = prefs.getStringSet("tasks", emptySet()) ?: emptySet()
        val taskList = existingTasks.map { stringToTask(it) }.toMutableList()

        // Поиск и обновление задачи в списке, или добавление новой задачи, если она не найдена
        val index = taskList.indexOfFirst { it.id == task.id }
        if (index != -1) {
            taskList[index] = task  // Замена существующей задачи, если ID совпадает
        } else {
            taskList.add(task)  // Добавление новой задачи, если задача с таким ID не найдена
        }

        // Сериализация списка задач обратно в строку
        val tasksAsStrings = taskList.map { t ->
            "${t.id}|${t.title}|${t.description}|${t.date}|${t.tags.joinToString(",")}|${t.priority}|${t.isStarred}"
        }.toSet()

        // Сохранение обновлённого списка задач
        editor.putStringSet("tasks", tasksAsStrings)
        editor.apply()
    }

    // Функция для десериализации строки задачи обратно в объект Task
    fun stringToTask(data: String): Task {
        val parts = data.split("|")
        return Task(
            id = parts[0].toInt(),
            title = parts[1],
            description = parts[2],
            date = parts[3],
            tags = parts[4].split(",").toList(),
            priority = parts[5],
            isStarred = parts[6].toBoolean()
        )
    }
    fun loadTasks(): List<Task> {
        val taskSet = prefs.getStringSet("tasks", emptySet())
        return taskSet?.map {
            val parts = it.split("|")
            Task(
                id = parts[0].toInt(),
                title = parts[1],
                description = parts[2],
                date = parts[3],
                tags = parts[4].split(",").filter { tag -> tag.isNotEmpty() },
                priority = parts[5],
                isStarred = parts[6].toBoolean()
            )
        } ?: emptyList()
    }
}
