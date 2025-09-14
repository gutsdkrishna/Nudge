# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**Nudge** is an Android to-do and reminder app built with Kotlin and Jetpack Compose. It's designed for OnePlus Nord 5 with AMOLED display and Always-On Display (AOD) support. The app provides persistent foreground notifications for task visibility on lockscreen/AOD.

### Key Specifications
- **Target Platform**: Android (OnePlus Nord 5, AMOLED with AOD support)
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material 3
- **Min SDK**: 31 (Android 12)
- **Target SDK**: 35
- **Package**: `com.kalki.nudge`
- **Build System**: Gradle with Kotlin DSL

## Build Commands

### Development
```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Install debug build on device
./gradlew installDebug

# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest

# Clean build
./gradlew clean
```

### Code Quality
```bash
# Lint check
./gradlew lint

# Lint and generate report
./gradlew lintDebug
```

## Architecture

The app follows **MVVM (Model-View-ViewModel)** pattern with these layers:

### Data Layer (to be implemented)
- `data/Task.kt` → Room Entity with fields: `id`, `title`, `description`, `reminderTime`, `isCompleted`
- `data/TaskDao.kt` → Database Access Object (insert, update, delete, getAll)
- `data/TaskDatabase.kt` → Room database configuration

### Repository Layer (to be implemented)
- `repository/TaskRepository.kt` → Bridges DAO and ViewModel, handles data operations

### Domain Layer (to be implemented)
- `viewmodel/TaskViewModel.kt` → Holds UI state, exposes Flow of tasks

### UI Layer (Compose)
- **Current**: `MainActivity.kt` with basic Greeting composable
- **Planned**: 
  - `ui/TaskListScreen.kt` → Main screen showing today's tasks
  - `ui/AddTaskScreen.kt` → Add/Edit task form with date/time picker
  - `ui/components/TaskItem.kt` → Reusable task item composable
  - `NavGraph.kt` → Compose navigation routes

### Services Layer (to be implemented)
- `notifications/ReminderWorker.kt` → WorkManager for scheduled reminders
- `notifications/NotificationUtils.kt` → Notification creation helpers
- `notifications/ForegroundService.kt` → Persistent notification service for lockscreen visibility

## Dependencies Management

Dependencies are managed through Gradle Version Catalogs (`gradle/libs.versions.toml`). Current setup includes:
- **Compose BOM**: `2024.09.00`
- **Kotlin**: `2.0.21`
- **AGP**: `8.10.1`
- **Core KTX**: `1.10.1`

### Missing Dependencies (to be added for full implementation)
- Room database components
- Navigation Compose
- WorkManager for background tasks
- Coroutines support

## Key Features to Implement

1. **Task Management**: Local persistence via Room DB
2. **Reminders**: WorkManager-based scheduling with notification actions (Mark Done/Snooze)
3. **Persistent Notification**: Foreground service showing today's tasks on lockscreen/AOD
4. **Material 3 Theming**: Dynamic colors with dark/light mode support

## Development Notes

- **Theme Configuration**: Uses Material 3 dynamic theming with Android 12+ support
- **Edge-to-Edge**: App uses `enableEdgeToEdge()` for modern Android UI
- **Target Device**: Optimized for OnePlus devices with OxygenOS battery optimization considerations
- **Notification Strategy**: Persistent foreground notifications for lockscreen visibility, with grouped notifications for multiple simultaneous reminders

## Navigation Flow

- **MainActivity** → **TaskListScreen** (default)
- **TaskListScreen** → **AddTaskScreen** (via FAB)
- **AddTaskScreen** → Save task → Schedule WorkManager → Return to TaskListScreen

The project is currently in initial setup phase with basic Compose structure. Full feature implementation following the MVVM architecture outlined above is needed to meet the PRD requirements.