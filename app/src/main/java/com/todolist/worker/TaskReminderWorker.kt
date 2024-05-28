package com.todolist.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.todolist.util.NotificationUtils

class TaskReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        // Retrieve the task title from the input data
        val taskTitle = inputData.getString("task_title") ?: return Result.failure()

        // Retrieve the task ID from the input data
        val taskId = inputData.getInt("task_id", -1)
        if (taskId == -1) return Result.failure()

        // Show the notification for the task
        NotificationUtils.showNotification(applicationContext, taskTitle, taskId)

        // Return success if the work is completed successfully
        return Result.success()
    }
}