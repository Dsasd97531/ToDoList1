package com.todolist.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.todolist.R

object NotificationUtils {
    private const val CHANNEL_ID = "todo_notifications_channel"
    private const val CHANNEL_NAME = "To-Do Notifications"
    private const val CHANNEL_DESC = "Notifications for To-Do List"

    // Create a notification channel for API level 26+ (Oreo and above)
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Define the notification channel with ID, name, and importance level
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESC
            }
            // Get the notification manager system service
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            // Create the notification channel
            notificationManager.createNotificationChannel(channel)
        }
    }

    // Show a notification for a specific task
    fun showNotification(context: Context, taskTitle: String, taskId: Int) {
        // Build the notification with title, content, and priority
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_icon)
            .setContentTitle("Task Reminder")
            .setContentText("Your task '$taskTitle' is due in 1 hour.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        // Get the notification manager and show the notification
        with(NotificationManagerCompat.from(context)) {
            // Check for notification permission before showing the notification
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            // Show the notification with a unique task ID
            notify(taskId, builder.build())
        }
    }
}