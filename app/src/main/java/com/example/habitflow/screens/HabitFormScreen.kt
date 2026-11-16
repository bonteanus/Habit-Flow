package com.example.habitflow.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.habitflow.data.HabitEntity
import com.example.habitflow.util.DateUtils
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitFormScreen(
    existingHabit: HabitEntity? = null,
    imageUri: String?,
    onBack: () -> Unit,
    onOpenCamera: () -> Unit,
    onRemovePhoto: () -> Unit,
    onSave: (
        name: String,
        category: String,
        targetTimesPerWeek: Int,
        imageUri: String?
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = listOf(
        "Health",
        "Study",
        "Fitness",
        "Wellbeing",
        "Productivity",
        "Personal"
    )

    val isEditing =
        existingHabit != null

    var habitName by rememberSaveable(
        existingHabit?.habitId
    ) {
        mutableStateOf(
            existingHabit?.name.orEmpty()
        )
    }

    var selectedCategory by rememberSaveable(
        existingHabit?.habitId
    ) {
        mutableStateOf(
            existingHabit?.category
                ?: categories.first()
        )
    }

    var targetTimesPerWeek by rememberSaveable(
        existingHabit?.habitId
    ) {
        mutableIntStateOf(
            existingHabit?.targetTimesPerWeek
                ?: 7
        )
    }

    var categoryMenuExpanded by remember {
        mutableStateOf(false)
    }

    val canSave =
        habitName.trim().isNotEmpty()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditing) {
                            "Edit habit"
                        } else {
                            "Create habit"
                        }
                    )
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(20.dp),
            verticalArrangement =
                Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Habit details",
                style =
                    MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = habitName,
                onValueChange = {
                    habitName = it
                },
                label = {
                    Text("Habit name")
                },
                placeholder = {
                    Text(
                        "Example: Drink more water"
                    )
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Column {
                Text(
                    text = "Category",
                    style =
                        MaterialTheme.typography.labelLarge
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                OutlinedButton(
                    onClick = {
                        categoryMenuExpanded = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.SpaceBetween
                    ) {
                        Text(selectedCategory)

                        Icon(
                            imageVector =
                                Icons.Default.ArrowDropDown,
                            contentDescription =
                                "Choose category"
                        )
                    }
                }

                DropdownMenu(
                    expanded = categoryMenuExpanded,
                    onDismissRequest = {
                        categoryMenuExpanded = false
                    }
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = {
                                Text(category)
                            },
                            onClick = {
                                selectedCategory =
                                    category

                                categoryMenuExpanded =
                                    false
                            }
                        )
                    }
                }
            }

            Column {
                Text(
                    text =
                        "Target: $targetTimesPerWeek times per week",
                    style =
                        MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text =
                        "Choose how many days each week you want to complete this habit.",
                    style =
                        MaterialTheme.typography.bodyMedium
                )

                Slider(
                    value =
                        targetTimesPerWeek.toFloat(),
                    onValueChange = { value ->
                        targetTimesPerWeek =
                            value
                                .roundToInt()
                                .coerceIn(1, 7)
                    },
                    valueRange = 1f..7f,
                    steps = 5,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.SpaceBetween
                ) {
                    Text("1 day")
                    Text("7 days")
                }
            }

            Column {
                Text(
                    text = "Start date",
                    style =
                        MaterialTheme.typography.labelLarge
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text =
                        existingHabit?.startDate
                            ?: DateUtils.todayIso(),
                    style =
                        MaterialTheme.typography.bodyLarge
                )

                Text(
                    text =
                        "The start date is assigned automatically.",
                    style =
                        MaterialTheme.typography.bodySmall,
                    color =
                        MaterialTheme.colorScheme
                            .onSurfaceVariant
                )
            }

            Column(
                verticalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Habit photo",
                    style =
                        MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text =
                        "You can attach an optional photograph to support your habit.",
                    style =
                        MaterialTheme.typography.bodyMedium
                )

                HabitPhoto(
                    imageUri = imageUri
                )

                Button(
                    onClick = onOpenCamera,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector =
                            Icons.Default.CameraAlt,
                        contentDescription = null
                    )

                    Text(
                        text = if (imageUri == null) {
                            " Take photo"
                        } else {
                            " Take another photo"
                        }
                    )
                }

                if (imageUri != null) {
                    OutlinedButton(
                        onClick = onRemovePhoto,
                        modifier =
                            Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector =
                                Icons.Default.Delete,
                            contentDescription = null
                        )

                        Text(" Remove photo")
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Button(
                onClick = {
                    onSave(
                        habitName.trim(),
                        selectedCategory,
                        targetTimesPerWeek,
                        imageUri
                    )
                },
                enabled = canSave,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isEditing) {
                        "Save changes"
                    } else {
                        "Save habit"
                    }
                )
            }
        }
    }
}