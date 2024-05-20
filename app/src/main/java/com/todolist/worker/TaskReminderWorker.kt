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
        val taskTitle = inputData.getString("task_title") ?: return Result.failure()
        val taskId = inputData.getInt("task_id", -1)
        if (taskId == -1) return Result.failure()

        NotificationUtils.showNotification(applicationContext, taskTitle, taskId)
        return Result.success()
    }
}
