package com.kalki.nudge.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.kalki.nudge.data.TaskDatabase
import com.kalki.nudge.repository.TaskRepository

class ReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    
    override suspend fun doWork(): Result {
        return try {
            val taskId = inputData.getLong("task_id", -1L)
            if (taskId == -1L) return Result.failure()
            
            val database = TaskDatabase.getDatabase(applicationContext)
            val repository = TaskRepository(database.taskDao())
            val task = repository.getTaskById(taskId)
            
            if (task != null && !task.isCompleted) {
                val notification = NotificationUtils.createReminderNotification(
                    context = applicationContext,
                    taskId = task.id,
                    title = task.title,
                    description = task.description
                )
                
                NotificationUtils.showNotification(
                    context = applicationContext,
                    notificationId = NotificationUtils.REMINDER_NOTIFICATION_ID,
                    notification = notification
                )
            }
            
            Result.success()
        } catch (exception: Exception) {
            Result.failure()
        }
    }
}