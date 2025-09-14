package com.kalki.nudge.notifications

import android.app.Service
import android.content.Intent
import android.os.IBinder
import kotlinx.coroutines.*
import com.kalki.nudge.data.TaskDatabase
import com.kalki.nudge.repository.TaskRepository

class PersistentNotificationService : Service() {
    
    private lateinit var repository: TaskRepository
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    override fun onCreate() {
        super.onCreate()
        
        val database = TaskDatabase.getDatabase(this)
        repository = TaskRepository(database.taskDao())
        
        // Create notification channels
        NotificationUtils.createNotificationChannels(this)
        
        // Start observing today's tasks
        startObservingTasks()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY // Restart if killed by system
    }
    
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
    
    private fun startObservingTasks() {
        serviceScope.launch {
            repository.getTodaysTasks().collect { tasks ->
                updatePersistentNotification(tasks)
            }
        }
    }
    
    private fun updatePersistentNotification(tasks: List<com.kalki.nudge.data.Task>) {
        val pendingTasks = tasks.filter { !it.isCompleted }
        val taskTitles = pendingTasks.map { task ->
            if (task.description.isNotBlank()) {
                "${task.title}: ${task.description}"
            } else {
                task.title
            }
        }
        
        val notification = NotificationUtils.createPersistentNotification(
            context = this,
            taskCount = pendingTasks.size,
            tasks = taskTitles
        )
        
        // Start foreground service with persistent notification
        startForeground(NotificationUtils.PERSISTENT_NOTIFICATION_ID, notification.build())
    }
    
    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        stopForeground(true)
    }
    
    companion object {
        fun startService(context: android.content.Context) {
            val intent = Intent(context, PersistentNotificationService::class.java)
            context.startForegroundService(intent)
        }
        
        fun stopService(context: android.content.Context) {
            val intent = Intent(context, PersistentNotificationService::class.java)
            context.stopService(intent)
        }
    }
}