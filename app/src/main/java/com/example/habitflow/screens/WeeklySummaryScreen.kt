package com.example.habitflow.screens

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.habitflow.data.HabitEntity
import com.example.habitflow.data.HabitLogEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklySummaryScreen(
    habits: List<HabitEntity>,
    logs: List<HabitLogEntity>,
    weekStart: String,
    weekEnd: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeHabits =
        habits.filter { habit ->
            habit.isActive
        }

    val completedLogs =
        logs.filter { log ->
            log.isCompleted
        }

    val totalWeeklyTarget =
        activeHabits.sumOf { habit ->
            habit.targetTimesPerWeek
        }

    val totalCompleted =
        completedLogs.size

    val overallProgress =
        if (totalWeeklyTarget == 0) {
            0f
        } else {
            (
                    totalCompleted.toFloat() /
                            totalWeeklyTarget.toFloat()
                    ).coerceIn(0f, 1f)
        }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text("Weekly summary")
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(
                            imageVector =
                                Icons.Default.ArrowBack,
                            contentDescription =
                                "Go back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 8.dp,
                bottom = 24.dp
            ),
            verticalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {
            item {
                OverallWeeklyCard(
                    weekStart = weekStart,
                    weekEnd = weekEnd,
                    completedCount =
                        totalCompleted,
                    targetCount =
                        totalWeeklyTarget,
                    progress =
                        overallProgress
                )
            }

            item {
                Text(
                    text = "Habit progress",
                    style =
                        MaterialTheme.typography
                            .titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier =
                        Modifier.padding(top = 8.dp)
                )
            }

            if (activeHabits.isEmpty()) {
                item {
                    Text(
                        text =
                            "Create a habit to begin tracking weekly progress.",
                        style =
                            MaterialTheme.typography
                                .bodyLarge,
                        modifier =
                            Modifier.padding(vertical = 24.dp)
                    )
                }
            } else {
                items(
                    items = activeHabits,
                    key = { habit ->
                        habit.habitId
                    }
                ) { habit ->

                    val habitCompletedCount =
                        completedLogs.count { log ->
                            log.habitOwnerId ==
                                    habit.habitId
                        }

                    HabitWeeklyCard(
                        habit = habit,
                        completedCount =
                            habitCompletedCount
                    )
                }
            }
        }
    }
}

@Composable
private fun OverallWeeklyCard(
    weekStart: String,
    weekEnd: String,
    completedCount: Int,
    targetCount: Int,
    progress: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme
                    .primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement =
                Arrangement.spacedBy(10.dp)
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
                    text = "  This week's progress",
                    style =
                        MaterialTheme.typography
                            .titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "$weekStart to $weekEnd",
                style =
                    MaterialTheme.typography.bodyMedium
            )

            Text(
                text =
                    "$completedCount completions against a target of $targetCount",
                style =
                    MaterialTheme.typography.bodyLarge
            )

            LinearProgressIndicator(
                progress = {
                    progress
                },
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text =
                    "${(progress * 100).toInt()}% of the weekly target",
                style =
                    MaterialTheme.typography.bodySmall,
                color =
                    MaterialTheme.colorScheme
                        .onSurfaceVariant
            )
        }
    }
}

@Composable
private fun HabitWeeklyCard(
    habit: HabitEntity,
    completedCount: Int,
    modifier: Modifier = Modifier
) {
    val target =
        habit.targetTimesPerWeek

    val progress =
        if (target == 0) {
            0f
        } else {
            (
                    completedCount.toFloat() /
                            target.toFloat()
                    ).coerceIn(0f, 1f)
        }

    val remaining =
        (target - completedCount)
            .coerceAtLeast(0)

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = habit.name,
                style =
                    MaterialTheme.typography
                        .titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = habit.category,
                style =
                    MaterialTheme.typography.bodyMedium,
                color =
                    MaterialTheme.colorScheme.primary
            )

            Text(
                text =
                    "$completedCount of $target completions",
                style =
                    MaterialTheme.typography.bodyMedium
            )

            LinearProgressIndicator(
                progress = {
                    progress
                },
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text =
                    if (remaining == 0) {
                        "Weekly target reached"
                    } else {
                        "$remaining remaining this week"
                    },
                style =
                    MaterialTheme.typography.bodySmall,
                color =
                    MaterialTheme.colorScheme
                        .onSurfaceVariant
            )

            Spacer(
                modifier = Modifier.height(2.dp)
            )
        }
    }
}