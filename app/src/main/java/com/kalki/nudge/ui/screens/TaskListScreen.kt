package com.kalki.nudge.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kalki.nudge.data.Task
import com.kalki.nudge.ui.components.TaskItem
import com.kalki.nudge.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    onNavigateToAddTask: () -> Unit,
    onNavigateToEditTask: (Long) -> Unit,
    viewModel: TaskViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val todaysTasks by viewModel.todaysTasks.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    var showMenu by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Today's Tasks") },
                actions = {
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "Menu")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Clear Completed") },
                            onClick = {
                                viewModel.deleteCompletedTasks()
                                showMenu = false
                            }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddTask,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Task")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (todaysTasks.isEmpty()) {
                EmptyTasksMessage(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                TasksList(
                    tasks = todaysTasks,
                    onTaskClick = onNavigateToEditTask,
                    onToggleComplete = { task ->
                        if (task.isCompleted) {
                            viewModel.markTaskAsPending(task.id)
                        } else {
                            viewModel.markTaskAsCompleted(task.id)
                        }
                    },
                    onDeleteTask = { task ->
                        viewModel.deleteTask(task)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            // Show error message if exists
            uiState.error?.let { error ->
                LaunchedEffect(error) {
                    // You can show a snackbar here
                    // For now, we'll just clear the error after showing it
                    viewModel.clearError()
                }
            }
        }
    }
}

@Composable
private fun TasksList(
    tasks: List<Task>,
    onTaskClick: (Long) -> Unit,
    onToggleComplete: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(
            items = tasks,
            key = { task -> task.id }
        ) { task ->
            TaskItem(
                task = task,
                onTaskClick = { onTaskClick(task.id) },
                onToggleComplete = { onToggleComplete(task) },
                onDeleteTask = { onDeleteTask(task) }
            )
        }
    }
}

@Composable
private fun EmptyTasksMessage(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No tasks for today",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tap the + button to add a new task",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}