package com.example.habitflow.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.habitflow.data.HabitEntity
import com.example.habitflow.data.HabitLogEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitDetailsScreen(
    habit: HabitEntity,
    logs: List<HabitLogEntity>,
    completedToday: Boolean,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleToday: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember {
        mutableStateOf(false)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text("Habit details")
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
                },
                actions = {
                    IconButton(
                        onClick = {
                            showDeleteDialog = true
                        }
                    ) {
                        Icon(
                            imageVector =
                                Icons.Default.Delete,
                            contentDescription =
                                "Delete habit"
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
            verticalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {
            item {
                HabitInformationCard(
                    habit = habit,
                    modifier = Modifier.padding(
                        horizontal = 16.dp,
                        vertical = 8.dp
                    )
                )
            }

            if (habit.imageUri != null) {
                item {
                    Column(
                        modifier = Modifier.padding(
                            horizontal = 16.dp
                        ),
                        verticalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Habit photo",
                            style =
                                MaterialTheme.typography
                                    .titleMedium,
                            fontWeight =
                                FontWeight.Bold
                        )

                        HabitPhoto(
                            imageUri = habit.imageUri
                        )
                    }
                }
            }

            item {
                TodayStatusCard(
                    completedToday =
                        completedToday,
                    onToggleToday =
                        onToggleToday,
                    modifier = Modifier.padding(
                        horizontal = 16.dp
                    )
                )
            }

            item {
                OutlinedButton(
                    onClick = onEdit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 16.dp
                        )
                ) {
                    Text("Edit habit")
                }
            }

            item {
                Text(
                    text = "Completion history",
                    style =
                        MaterialTheme.typography
                            .titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 12.dp
                    )
                )
            }

            if (logs.isEmpty()) {
                item {
                    Text(
                        text =
                            "No progress has been logged yet.",
                        style =
                            MaterialTheme.typography
                                .bodyLarge,
                        modifier =
                            Modifier.padding(16.dp)
                    )
                }
            } else {
                items(
                    items = logs,
                    key = { log ->
                        log.logId
                    }
                ) { log ->
                    HistoryItem(
                        log = log,
                        modifier = Modifier.padding(
                            horizontal = 16.dp
                        )
                    )
                }
            }

            item {
                Spacer(
                    modifier =
                        Modifier.height(24.dp)
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
            },
            title = {
                Text("Delete habit?")
            },
            text = {
                Text(
                    text =
                        "This will also remove all progress logs for this habit."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDelete()
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun HabitInformationCard(
    habit: HabitEntity,
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
                Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = habit.name,
                style =
                    MaterialTheme.typography
                        .headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text =
                    "Category: ${habit.category}"
            )

            Text(
                text =
                    "Target: ${habit.targetTimesPerWeek} times per week"
            )

            Text(
                text =
                    "Started: ${habit.startDate}"
            )
        }
    }
}

@Composable
private fun TodayStatusCard(
    completedToday: Boolean,
    onToggleToday: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
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
                    onToggleToday()
                }
            )

            Column(
                modifier =
                    Modifier.padding(start = 8.dp)
            ) {
                Text(
                    text = "Today's status",
                    style =
                        MaterialTheme.typography
                            .titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = if (completedToday) {
                        "Completed"
                    } else {
                        "Not completed"
                    }
                )
            }
        }
    }
}

@Composable
private fun HistoryItem(
    log: HabitLogEntity,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {
                Text(
                    text = log.logDate,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = if (log.isCompleted) {
                        "Completed"
                    } else {
                        "Not completed"
                    },
                    color = if (log.isCompleted) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    }
                )
            }

            HorizontalDivider(
                modifier =
                    Modifier.padding(top = 8.dp)
            )
        }
    }
}