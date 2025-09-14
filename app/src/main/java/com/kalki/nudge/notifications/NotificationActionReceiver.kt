package com.kalki.nudge.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.kalki.nudge.data.TaskDatabase
import com.kalki.nudge.repository.TaskRepository
import java.util.concurrent.TimeUnit

class NotificationActionReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getLongExtra(NotificationUtils.EXTRA_TASK_ID, -1L)
        if (taskId == -1L) return
        
        when (intent.action) {
            NotificationUtils.ACTION_MARK_COMPLETE -> {
                markTaskComplete(context, taskId)
                NotificationUtils.cancelNotification(context, NotificationUtils.REMINDER_NOTIFICATION_ID)
            }
            NotificationUtils.ACTION_SNOOZE -> {
                snoozeTask(context, taskId)
                NotificationUtils.cancelNotification(context, NotificationUtils.REMINDER_NOTIFICATION_ID)
            }
        }
    }
    
    private fun markTaskComplete(context: Context, taskId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val database = TaskDatabase.getDatabase(context)
            val repository = TaskRepository(database.taskDao())
            repository.markTaskAsCompleted(taskId)
        }
    }
    
    private fun snoozeTask(context: Context, taskId: Long) {
        // Schedule a new reminder 15 minutes from now
        val snoozeWorkRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(15, TimeUnit.MINUTES)
            .setInputData(workDataOf("task_id" to taskId))
            .build()
        
        WorkManager.getInstance(context).enqueue(snoozeWorkRequest)
    }
}