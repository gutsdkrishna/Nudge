package com.kalki.nudge.repository

import com.kalki.nudge.data.Task
import com.kalki.nudge.data.TaskDao
import kotlinx.coroutines.flow.Flow
import java.util.Date

class TaskRepository(private val taskDao: TaskDao) {
    
    fun getAllTasks(): Flow<List<Task>> = taskDao.getAllTasks()
    
    fun getTodaysTasks(): Flow<List<Task>> = taskDao.getTodaysTasks()
    
    fun getPendingTasks(): Flow<List<Task>> = taskDao.getPendingTasks()
    
    suspend fun getTaskById(id: Long): Task? = taskDao.getTaskById(id)
    
    suspend fun insertTask(task: Task): Long = taskDao.insertTask(task)
    
    suspend fun updateTask(task: Task) = taskDao.updateTask(task)
    
    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)
    
    suspend fun markTaskAsCompleted(id: Long) {
        taskDao.updateTaskCompletionStatus(id, true, Date())
    }
    
    suspend fun markTaskAsPending(id: Long) {
        taskDao.updateTaskCompletionStatus(id, false, Date())
    }
    
    suspend fun deleteCompletedTasks() = taskDao.deleteCompletedTasks()
}