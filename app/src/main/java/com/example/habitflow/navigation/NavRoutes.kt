package com.example.habitflow.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface AppRoute : NavKey

@Serializable
data object HomeRoute : AppRoute

@Serializable
data object AddHabitRoute : AppRoute

@Serializable
data object WeeklySummaryRoute : AppRoute

@Serializable
data object CameraRoute : AppRoute

@Serializable
data class HabitDetailsRoute(
    val habitId: Long
) : AppRoute

@Serializable
data class EditHabitRoute(
    val habitId: Long
) : AppRoute