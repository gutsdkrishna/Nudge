package com.kalki.nudge.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface TaskDao {
    
    @Query("SELECT * FROM tasks ORDER BY reminderTime ASC, createdAt DESC")
    fun getAllTasks(): Flow<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE DATE(reminderTime/1000, 'unixepoch') = DATE('now') ORDER BY reminderTime ASC")
    fun getTodaysTasks(): Flow<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE isCompleted = 0 ORDER BY reminderTime ASC")
    fun getPendingTasks(): Flow<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): Task?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long
    
    @Update
    suspend fun updateTask(task: Task)
    
    @Delete
    suspend fun deleteTask(task: Task)
    
    @Query("UPDATE tasks SET isCompleted = :isCompleted, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateTaskCompletionStatus(id: Long, isCompleted: Boolean, updatedAt: Date = Date())
    
    @Query("DELETE FROM tasks WHERE isCompleted = 1")
    suspend fun deleteCompletedTasks()
}