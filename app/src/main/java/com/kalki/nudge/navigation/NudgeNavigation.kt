package com.kalki.nudge.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kalki.nudge.ui.screens.AddTaskScreen
import com.kalki.nudge.ui.screens.TaskListScreen

sealed class Screen(val route: String) {
    object TaskList : Screen("task_list")
    object AddTask : Screen("add_task")
    object EditTask : Screen("edit_task/{taskId}") {
        fun createRoute(taskId: Long) = "edit_task/$taskId"
    }
}

@Composable
fun NudgeNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.TaskList.route
    ) {
        composable(Screen.TaskList.route) {
            TaskListScreen(
                onNavigateToAddTask = {
                    navController.navigate(Screen.AddTask.route)
                },
                onNavigateToEditTask = { taskId ->
                    navController.navigate(Screen.EditTask.createRoute(taskId))
                }
            )
        }
        
        composable(Screen.AddTask.route) {
            AddTaskScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.EditTask.route) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId")?.toLongOrNull()
            if (taskId != null) {
                EditTaskScreen(
                    taskId = taskId,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

@Composable
fun EditTaskScreen(
    taskId: Long,
    onNavigateBack: () -> Unit
) {
    // For now, reuse AddTaskScreen - we can enhance this later
    // In a full implementation, this would load the existing task data
    AddTaskScreen(onNavigateBack = onNavigateBack)
}