package com.kalki.nudge.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.kalki.nudge.data.Task
import com.kalki.nudge.data.TaskDatabase
import com.kalki.nudge.notifications.PersistentNotificationService
import com.kalki.nudge.notifications.ReminderWorker
import com.kalki.nudge.repository.TaskRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date
import java.util.concurrent.TimeUnit

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: TaskRepository
    
    init {
        val database = TaskDatabase.getDatabase(application)
        repository = TaskRepository(database.taskDao())
        
        // Start persistent notification service
        PersistentNotificationService.startService(application)
    }
    
    val allTasks: StateFlow<List<Task>> = repository.getAllTasks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    val todaysTasks: StateFlow<List<Task>> = repository.getTodaysTasks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    val pendingTasks: StateFlow<List<Task>> = repository.getPendingTasks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()
    
    fun insertTask(title: String, description: String, reminderTime: Date?) {
        viewModelScope.launch {
            try {
                val task = Task(
                    title = title.trim(),
                    description = description.trim(),
                    reminderTime = reminderTime,
                    createdAt = Date(),
                    updatedAt = Date()
                )
                val taskId = repository.insertTask(task)
                
                // Schedule notification if reminder time is set
                reminderTime?.let { reminderDateTime ->
                    scheduleReminder(taskId, reminderDateTime)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun updateTask(task: Task) {
        viewModelScope.launch {
            try {
                val updatedTask = task.copy(updatedAt = Date())
                repository.updateTask(updatedTask)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun deleteTask(task: Task) {
        viewModelScope.launch {
            try {
                repository.deleteTask(task)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun markTaskAsCompleted(taskId: Long) {
        viewModelScope.launch {
            try {
                repository.markTaskAsCompleted(taskId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun markTaskAsPending(taskId: Long) {
        viewModelScope.launch {
            try {
                repository.markTaskAsPending(taskId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun deleteCompletedTasks() {
        viewModelScope.launch {
            try {
                repository.deleteCompletedTasks()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    private fun scheduleReminder(taskId: Long, reminderTime: Date) {
        val currentTime = System.currentTimeMillis()
        val reminderTimeMillis = reminderTime.time
        val delay = reminderTimeMillis - currentTime
        
        if (delay > 0) {
            val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(workDataOf("task_id" to taskId))
                .build()
            
            WorkManager.getInstance(getApplication()).enqueue(workRequest)
        }
    }
}

data class TaskUiState(
    val isLoading: Boolean = false,
    val error: String? = null
)