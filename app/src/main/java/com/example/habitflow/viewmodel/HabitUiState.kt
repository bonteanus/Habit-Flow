package com.example.habitflow.viewmodel

import com.example.habitflow.data.HabitEntity
import com.example.habitflow.data.HabitLogEntity

data class HabitUiState(
    val habits: List<HabitEntity> = emptyList(),

    val todayLogsByHabitId: Map<Long, HabitLogEntity> =
        emptyMap(),

    val isLoading: Boolean = true,

    val errorMessage: String? = null
) {

    val completedTodayCount: Int
        get() = todayLogsByHabitId
            .values
            .count { log -> log.isCompleted }

    fun isCompletedToday(habitId: Long): Boolean {
        return todayLogsByHabitId[habitId]
            ?.isCompleted == true
    }
}