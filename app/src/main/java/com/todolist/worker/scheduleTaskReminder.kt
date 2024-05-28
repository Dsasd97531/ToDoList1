package com.todolist.worker

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.todolist.worker.TaskReminderWorker
import java.util.concurrent.TimeUnit

fun scheduleTaskReminder(context: Context, taskTitle: String, taskId: Int, taskDueTime: Long) {
    // Get the current system time
    val currentTime = System.currentTimeMillis()

    // Flag for testing; change to 'false' for production
    val isTesting = true

    // Calculate the delay for the reminder notification
    val delay = if (isTesting) {
        // For testing, set a short delay (10 seconds)
        TimeUnit.SECONDS.toMillis(10)
    } else {
        // For production, set the delay to 1 hour before the task's due time
        taskDueTime - currentTime - TimeUnit.HOURS.toMillis(1)
    }

    // If the delay is negative or zero, return without scheduling the reminder
    if (delay <= 0) return

    // Create input data for the worker
    val data = Data.Builder()
        .putString("task_title", taskTitle)
        .putInt("task_id", taskId)
        .build()

    // Create a OneTimeWorkRequest for the TaskReminderWorker
    val reminderRequest: WorkRequest = OneTimeWorkRequestBuilder<TaskReminderWorker>()
        .setInitialDelay(delay, TimeUnit.MILLISECONDS) // Set the initial delay
        .setInputData(data) // Set the input data
        .build()

    // Enqueue the work request using WorkManager
    WorkManager.getInstance(context).enqueue(reminderRequest)
}