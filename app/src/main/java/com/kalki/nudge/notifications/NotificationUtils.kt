package com.kalki.nudge.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.kalki.nudge.MainActivity
import com.kalki.nudge.R

object NotificationUtils {
    
    const val REMINDER_CHANNEL_ID = "reminder_channel"
    const val PERSISTENT_CHANNEL_ID = "persistent_channel"
    
    const val REMINDER_NOTIFICATION_ID = 1001
    const val PERSISTENT_NOTIFICATION_ID = 1002
    
    const val ACTION_MARK_COMPLETE = "ACTION_MARK_COMPLETE"
    const val ACTION_SNOOZE = "ACTION_SNOOZE"
    const val EXTRA_TASK_ID = "EXTRA_TASK_ID"
    
    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            
            // Reminder notification channel
            val reminderChannel = NotificationChannel(
                REMINDER_CHANNEL_ID,
                "Task Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for task reminders"
                enableVibration(true)
                enableLights(true)
            }
            
            // Persistent notification channel
            val persistentChannel = NotificationChannel(
                PERSISTENT_CHANNEL_ID,
                "Daily Tasks",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Persistent notification showing today's tasks"
                setShowBadge(false)
            }
            
            notificationManager.createNotificationChannel(reminderChannel)
            notificationManager.createNotificationChannel(persistentChannel)
        }
    }
    
    fun createReminderNotification(
        context: Context,
        taskId: Long,
        title: String,
        description: String
    ): NotificationCompat.Builder {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Mark Complete Action
        val markCompleteIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = ACTION_MARK_COMPLETE
            putExtra(EXTRA_TASK_ID, taskId)
        }
        val markCompletePendingIntent = PendingIntent.getBroadcast(
            context,
            taskId.toInt(),
            markCompleteIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Snooze Action
        val snoozeIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = ACTION_SNOOZE
            putExtra(EXTRA_TASK_ID, taskId)
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            (taskId + 10000).toInt(),
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Builder(context, REMINDER_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(description.ifBlank { "Task reminder" })
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .addAction(
                R.drawable.ic_launcher_foreground,
                "Mark Done",
                markCompletePendingIntent
            )
            .addAction(
                R.drawable.ic_launcher_foreground,
                "Snooze",
                snoozePendingIntent
            )
    }
    
    fun createPersistentNotification(
        context: Context,
        taskCount: Int,
        tasks: List<String>
    ): NotificationCompat.Builder {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val contentText = when {
            taskCount == 0 -> "No tasks for today"
            taskCount == 1 -> "1 task for today"
            else -> "$taskCount tasks for today"
        }
        
        val builder = NotificationCompat.Builder(context, PERSISTENT_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Nudge - Daily Tasks")
            .setContentText(contentText)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setShowWhen(false)
        
        // Add big text style if there are tasks
        if (tasks.isNotEmpty()) {
            val bigTextStyle = NotificationCompat.BigTextStyle()
                .bigText(tasks.joinToString("\n• ", "• "))
                .setSummaryText(contentText)
            builder.setStyle(bigTextStyle)
        }
        
        return builder
    }
    
    fun showNotification(context: Context, notificationId: Int, notification: NotificationCompat.Builder) {
        if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            NotificationManagerCompat.from(context).notify(notificationId, notification.build())
        }
    }
    
    fun cancelNotification(context: Context, notificationId: Int) {
        NotificationManagerCompat.from(context).cancel(notificationId)
    }
}