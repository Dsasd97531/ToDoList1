package com.todolist.worker

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.todolist.worker.TaskReminderWorker
import java.util.concurrent.TimeUnit

fun scheduleTaskReminder(context: Context, taskTitle: String, taskId: Int, taskDueTime: Long) {
    val currentTime = System.currentTimeMillis()
    val isTesting = true
    val delay = if (isTesting) {
        TimeUnit.SECONDS.toMillis(10)
    } else {
        taskDueTime - System.currentTimeMillis() - TimeUnit.HOURS.toMillis(1)
    }
    if (delay <= 0) return

    val data = Data.Builder()
        .putString("task_title", taskTitle)
        .putInt("task_id", taskId)
        .build()

    val reminderRequest: WorkRequest = OneTimeWorkRequestBuilder<TaskReminderWorker>()
        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
        .setInputData(data)
        .build()

    WorkManager.getInstance(context).enqueue(reminderRequest)
}
