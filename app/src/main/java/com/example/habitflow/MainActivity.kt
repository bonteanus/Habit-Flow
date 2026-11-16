package com.example.habitflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.habitflow.camera.CameraViewModel
import com.example.habitflow.data.DatabaseProvider
import com.example.habitflow.navigation.AddHabitRoute
import com.example.habitflow.navigation.CameraRoute
import com.example.habitflow.navigation.EditHabitRoute
import com.example.habitflow.navigation.HabitDetailsRoute
import com.example.habitflow.navigation.HomeRoute
import com.example.habitflow.navigation.WeeklySummaryRoute
import com.example.habitflow.screens.CameraScreen
import com.example.habitflow.screens.HabitDetailsScreen
import com.example.habitflow.screens.HabitFormScreen
import com.example.habitflow.screens.HomeScreen
import com.example.habitflow.screens.WeeklySummaryScreen
import com.example.habitflow.ui.theme.HabitFlowTheme
import com.example.habitflow.util.DateUtils
import com.example.habitflow.viewmodel.HabitViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val habitDao =
            DatabaseProvider
                .getDatabase(applicationContext)
                .habitDao()

        val habitViewModelFactory =
            viewModelFactory {
                initializer {
                    HabitViewModel(habitDao)
                }
            }

        setContent {
            HabitFlowTheme {
                val habitViewModel:
                        HabitViewModel = viewModel(
                    factory =
                        habitViewModelFactory
                )

                val uiState by
                habitViewModel
                    .uiState
                    .collectAsStateWithLifecycle()

                val backStack =
                    rememberNavBackStack(
                        HomeRoute
                    )

                var pendingImageUri by
                rememberSaveable {
                    mutableStateOf<String?>(
                        null
                    )
                }

                NavDisplay(
                    backStack = backStack,
                    onBack = {
                        if (backStack.size > 1) {
                            backStack
                                .removeLastOrNull()
                        }
                    },
                    entryProvider =
                        entryProvider {

                            entry<HomeRoute> {
                                HomeScreen(
                                    uiState = uiState,
                                    onAddHabit = {
                                        pendingImageUri =
                                            null

                                        backStack.add(
                                            AddHabitRoute
                                        )
                                    },
                                    onOpenHabit = {
                                            habitId ->

                                        backStack.add(
                                            HabitDetailsRoute(
                                                habitId =
                                                    habitId
                                            )
                                        )
                                    },
                                    onOpenWeeklySummary = {
                                        backStack.add(
                                            WeeklySummaryRoute
                                        )
                                    },
                                    onToggleHabit = {
                                            habitId ->

                                        habitViewModel
                                            .toggleHabitForToday(
                                                habitId
                                            )
                                    },
                                    onClearError = {
                                        habitViewModel
                                            .clearError()
                                    }
                                )
                            }

                            entry<AddHabitRoute> {
                                HabitFormScreen(
                                    imageUri =
                                        pendingImageUri,
                                    onBack = {
                                        pendingImageUri =
                                            null

                                        backStack
                                            .removeLastOrNull()
                                    },
                                    onOpenCamera = {
                                        backStack.add(
                                            CameraRoute
                                        )
                                    },
                                    onRemovePhoto = {
                                        pendingImageUri =
                                            null
                                    },
                                    onSave = {
                                            name,
                                            category,
                                            target,
                                            imageUri ->

                                        habitViewModel
                                            .addHabit(
                                                name = name,
                                                category =
                                                    category,
                                                targetTimesPerWeek =
                                                    target,
                                                imageUri =
                                                    imageUri
                                            )

                                        pendingImageUri =
                                            null

                                        backStack
                                            .removeLastOrNull()
                                    }
                                )
                            }

                            entry<CameraRoute> {
                                val cameraViewModel:
                                        CameraViewModel =
                                    viewModel()

                                CameraScreen(
                                    cameraViewModel =
                                        cameraViewModel,
                                    onBack = {
                                        backStack
                                            .removeLastOrNull()
                                    },
                                    onPhotoCaptured = {
                                            capturedUri ->

                                        pendingImageUri =
                                            capturedUri

                                        backStack
                                            .removeLastOrNull()
                                    }
                                )
                            }

                            entry<WeeklySummaryRoute> {
                                val weekRange =
                                    remember {
                                        DateUtils
                                            .currentWeekRange()
                                    }

                                val weeklyLogsFlow =
                                    remember(
                                        weekRange.first,
                                        weekRange.second
                                    ) {
                                        habitViewModel
                                            .observeLogsBetweenDates(
                                                startDate =
                                                    weekRange.first,
                                                endDate =
                                                    weekRange.second
                                            )
                                    }

                                val weeklyLogs by
                                weeklyLogsFlow
                                    .collectAsStateWithLifecycle(
                                        initialValue =
                                            emptyList()
                                    )

                                WeeklySummaryScreen(
                                    habits =
                                        uiState.habits,
                                    logs =
                                        weeklyLogs,
                                    weekStart =
                                        weekRange.first,
                                    weekEnd =
                                        weekRange.second,
                                    onBack = {
                                        backStack
                                            .removeLastOrNull()
                                    }
                                )
                            }

                            entry<HabitDetailsRoute> {
                                    route ->

                                val habitFlow =
                                    remember(
                                        route.habitId
                                    ) {
                                        habitViewModel
                                            .observeHabitById(
                                                route.habitId
                                            )
                                    }

                                val logsFlow =
                                    remember(
                                        route.habitId
                                    ) {
                                        habitViewModel
                                            .observeLogsForHabit(
                                                route.habitId
                                            )
                                    }

                                val habit by
                                habitFlow
                                    .collectAsStateWithLifecycle(
                                        initialValue = null
                                    )

                                val logs by
                                logsFlow
                                    .collectAsStateWithLifecycle(
                                        initialValue =
                                            emptyList()
                                    )

                                val selectedHabit =
                                    habit

                                if (selectedHabit == null) {
                                    LoadingScreen()
                                } else {
                                    HabitDetailsScreen(
                                        habit =
                                            selectedHabit,
                                        logs = logs,
                                        completedToday =
                                            uiState
                                                .isCompletedToday(
                                                    route.habitId
                                                ),
                                        onBack = {
                                            backStack
                                                .removeLastOrNull()
                                        },
                                        onEdit = {
                                            pendingImageUri =
                                                selectedHabit
                                                    .imageUri

                                            backStack.add(
                                                EditHabitRoute(
                                                    habitId =
                                                        route
                                                            .habitId
                                                )
                                            )
                                        },
                                        onDelete = {
                                            habitViewModel
                                                .deleteHabit(
                                                    selectedHabit
                                                )

                                            pendingImageUri =
                                                null

                                            while (
                                                backStack.size > 1
                                            ) {
                                                backStack
                                                    .removeLastOrNull()
                                            }
                                        },
                                        onToggleToday = {
                                            habitViewModel
                                                .toggleHabitForToday(
                                                    route.habitId
                                                )
                                        }
                                    )
                                }
                            }

                            entry<EditHabitRoute> {
                                    route ->

                                val habitFlow =
                                    remember(
                                        route.habitId
                                    ) {
                                        habitViewModel
                                            .observeHabitById(
                                                route.habitId
                                            )
                                    }

                                val habit by
                                habitFlow
                                    .collectAsStateWithLifecycle(
                                        initialValue = null
                                    )

                                val selectedHabit =
                                    habit

                                if (selectedHabit == null) {
                                    LoadingScreen()
                                } else {
                                    HabitFormScreen(
                                        existingHabit =
                                            selectedHabit,
                                        imageUri =
                                            pendingImageUri,
                                        onBack = {
                                            pendingImageUri =
                                                null

                                            backStack
                                                .removeLastOrNull()
                                        },
                                        onOpenCamera = {
                                            backStack.add(
                                                CameraRoute
                                            )
                                        },
                                        onRemovePhoto = {
                                            pendingImageUri =
                                                null
                                        },
                                        onSave = {
                                                name,
                                                category,
                                                target,
                                                imageUri ->

                                            habitViewModel
                                                .updateHabit(
                                                    originalHabit =
                                                        selectedHabit,
                                                    name = name,
                                                    category =
                                                        category,
                                                    targetTimesPerWeek =
                                                        target,
                                                    imageUri =
                                                        imageUri
                                                )

                                            pendingImageUri =
                                                null

                                            backStack
                                                .removeLastOrNull()
                                        }
                                    )
                                }
                            }
                        }
                )
            }
        }
    }
}

@Composable
private fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}