package com.example.habitflow.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.habitflow.data.HabitEntity
import com.example.habitflow.viewmodel.HabitUiState

private enum class HabitFilter {
    ALL,
    COMPLETED,
    NOT_COMPLETED
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: HabitUiState,
    onAddHabit: () -> Unit,
    onOpenHabit: (Long) -> Unit,
    onOpenWeeklySummary: () -> Unit,
    onToggleHabit: (Long) -> Unit,
    onClearError: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState =
        remember {
            SnackbarHostState()
        }

    var selectedFilter by rememberSaveable {
        mutableStateOf(HabitFilter.ALL)
    }

    LaunchedEffect(uiState.errorMessage) {
        val message = uiState.errorMessage

        if (message != null) {
            snackbarHostState.showSnackbar(message)
            onClearError()
        }
    }

    val activeHabits =
        uiState.habits.filter { habit ->
            habit.isActive
        }

    val filteredHabits =
        when (selectedFilter) {
            HabitFilter.ALL -> {
                activeHabits
            }

            HabitFilter.COMPLETED -> {
                activeHabits.filter { habit ->
                    uiState.isCompletedToday(
                        habit.habitId
                    )
                }
            }

            HabitFilter.NOT_COMPLETED -> {
                activeHabits.filter { habit ->
                    !uiState.isCompletedToday(
                        habit.habitId
                    )
                }
            }
        }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "HabitFlow",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(
                        onClick = onOpenWeeklySummary
                    ) {
                        Icon(
                            imageVector =
                                Icons.Default.DateRange,
                            contentDescription =
                                "Open weekly summary"
                        )
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddHabit
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add habit"
                )
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            DailySummaryCard(
                completedCount =
                    uiState.completedTodayCount,
                totalCount = activeHabits.size,
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 8.dp
                )
            )

            HabitFilterBar(
                selectedFilter = selectedFilter,
                onFilterSelected = { filter ->
                    selectedFilter = filter
                },
                modifier = Modifier.padding(
                    horizontal = 16.dp
                )
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            when {
                uiState.isLoading -> {
                    LoadingContent(
                        modifier = Modifier.weight(1f)
                    )
                }

                activeHabits.isEmpty() -> {
                    EmptyHabitContent(
                        modifier = Modifier.weight(1f)
                    )
                }

                filteredHabits.isEmpty() -> {
                    EmptyFilterContent(
                        modifier = Modifier.weight(1f)
                    )
                }

                else -> {
                    HabitList(
                        habits = filteredHabits,
                        uiState = uiState,
                        onOpenHabit = onOpenHabit,
                        onToggleHabit = onToggleHabit,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun DailySummaryCard(
    completedCount: Int,
    totalCount: Int,
    modifier: Modifier = Modifier
) {
    val progress =
        if (totalCount == 0) {
            0f
        } else {
            completedCount.toFloat() /
                    totalCount.toFloat()
        }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme
                    .primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                Icon(
                    imageVector =
                        Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint =
                        MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "  Today's progress",
                    style =
                        MaterialTheme.typography
                            .titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text =
                    "$completedCount of $totalCount habits completed",
                style =
                    MaterialTheme.typography.bodyLarge
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            LinearProgressIndicator(
                progress = {
                    progress
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun HabitFilterBar(
    selectedFilter: HabitFilter,
    onFilterSelected: (HabitFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement =
            Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected =
                selectedFilter == HabitFilter.ALL,
            onClick = {
                onFilterSelected(HabitFilter.ALL)
            },
            label = {
                Text("All")
            }
        )

        FilterChip(
            selected =
                selectedFilter ==
                        HabitFilter.COMPLETED,
            onClick = {
                onFilterSelected(
                    HabitFilter.COMPLETED
                )
            },
            label = {
                Text("Completed")
            }
        )

        FilterChip(
            selected =
                selectedFilter ==
                        HabitFilter.NOT_COMPLETED,
            onClick = {
                onFilterSelected(
                    HabitFilter.NOT_COMPLETED
                )
            },
            label = {
                Text("Remaining")
            }
        )
    }
}

@Composable
private fun HabitList(
    habits: List<HabitEntity>,
    uiState: HabitUiState,
    onOpenHabit: (Long) -> Unit,
    onToggleHabit: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 4.dp,
            bottom = 96.dp
        ),
        verticalArrangement =
            Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = habits,
            key = { habit ->
                habit.habitId
            }
        ) { habit ->

            HabitCard(
                habit = habit,
                completedToday =
                    uiState.isCompletedToday(
                        habit.habitId
                    ),
                onOpen = {
                    onOpenHabit(habit.habitId)
                },
                onToggle = {
                    onToggleHabit(habit.habitId)
                }
            )
        }
    }
}

@Composable
private fun HabitCard(
    habit: HabitEntity,
    completedToday: Boolean,
    onOpen: () -> Unit,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onOpen,
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            Checkbox(
                checked = completedToday,
                onCheckedChange = {
                    onToggle()
                }
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                Text(
                    text = habit.name,
                    style =
                        MaterialTheme.typography
                            .titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = habit.category,
                    style =
                        MaterialTheme.typography
                            .bodyMedium,
                    color =
                        MaterialTheme.colorScheme.primary
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text =
                        "${habit.targetTimesPerWeek} times per week",
                    style =
                        MaterialTheme.typography
                            .bodySmall
                )

                Text(
                    text =
                        "Started ${habit.startDate}",
                    style =
                        MaterialTheme.typography
                            .bodySmall,
                    color =
                        MaterialTheme.colorScheme
                            .onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun LoadingContent(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyHabitContent(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {
            Text(
                text = "No habits yet",
                style =
                    MaterialTheme.typography
                        .headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text =
                    "Press the + button to create your first habit.",
                style =
                    MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun EmptyFilterContent(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No habits match this filter.",
            style =
                MaterialTheme.typography.bodyLarge
        )
    }
}