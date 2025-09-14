package com.kalki.nudge.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val reminderTime: Date?,
    val isCompleted: Boolean = false,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)