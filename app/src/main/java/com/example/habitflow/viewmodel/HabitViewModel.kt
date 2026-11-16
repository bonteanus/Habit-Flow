package com.example.habitflow.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitflow.data.HabitDao
import com.example.habitflow.data.HabitEntity
import com.example.habitflow.data.HabitLogEntity
import com.example.habitflow.util.DateUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HabitViewModel(
    private val habitDao: HabitDao
) : ViewModel() {

    private val errorMessage =
        MutableStateFlow<String?>(null)

    private val todayDate =
        DateUtils.todayIso()

    val uiState: StateFlow<HabitUiState> =
        combine(
            habitDao.observeAllHabits(),
            habitDao.observeLogsForDate(todayDate),
            errorMessage
        ) { habits, todayLogs, currentError ->

            HabitUiState(
                habits = habits,
                todayLogsByHabitId =
                    todayLogs.associateBy { log ->
                        log.habitOwnerId
                    },
                isLoading = false,
                errorMessage = currentError
            )
        }
            .catch { exception ->
                emit(
                    HabitUiState(
                        isLoading = false,
                        errorMessage =
                            exception.message
                                ?: "Unable to load habits."
                    )
                )
            }
            .stateIn(
                scope = viewModelScope,
                started =
                    SharingStarted.WhileSubscribed(5_000),
                initialValue = HabitUiState()
            )

    fun observeHabitById(
        habitId: Long
    ): Flow<HabitEntity?> {
        return habitDao.observeHabitById(habitId)
    }

    fun observeLogsForHabit(
        habitId: Long
    ): Flow<List<HabitLogEntity>> {
        return habitDao.observeLogsForHabit(habitId)
    }

    fun observeLogsBetweenDates(
        startDate: String,
        endDate: String
    ): Flow<List<HabitLogEntity>> {
        return habitDao.observeLogsBetweenDates(
            startDate = startDate,
            endDate = endDate
        )
    }

    fun addHabit(
        name: String,
        category: String,
        targetTimesPerWeek: Int,
        imageUri: String? = null
    ) {
        val validName = name.trim()
        val validCategory = category.trim()

        when {
            validName.isEmpty() -> {
                errorMessage.value =
                    "Please enter a habit name."
                return
            }

            validCategory.isEmpty() -> {
                errorMessage.value =
                    "Please select a category."
                return
            }

            targetTimesPerWeek !in 1..7 -> {
                errorMessage.value =
                    "The weekly target must be between 1 and 7."
                return
            }
        }

        viewModelScope.launch {
            try {
                habitDao.insertHabit(
                    HabitEntity(
                        name = validName,
                        category = validCategory,
                        startDate = DateUtils.todayIso(),
                        targetTimesPerWeek =
                            targetTimesPerWeek,
                        imageUri = imageUri,
                        isActive = true
                    )
                )

                errorMessage.value = null
            } catch (exception: Exception) {
                errorMessage.value =
                    exception.message
                        ?: "Unable to create the habit."
            }
        }
    }

    fun updateHabit(
        originalHabit: HabitEntity,
        name: String,
        category: String,
        targetTimesPerWeek: Int,
        imageUri: String? = originalHabit.imageUri
    ) {
        val validName = name.trim()
        val validCategory = category.trim()

        when {
            validName.isEmpty() -> {
                errorMessage.value =
                    "Please enter a habit name."
                return
            }

            validCategory.isEmpty() -> {
                errorMessage.value =
                    "Please select a category."
                return
            }

            targetTimesPerWeek !in 1..7 -> {
                errorMessage.value =
                    "The weekly target must be between 1 and 7."
                return
            }
        }

        viewModelScope.launch {
            try {
                habitDao.updateHabit(
                    originalHabit.copy(
                        name = validName,
                        category = validCategory,
                        targetTimesPerWeek =
                            targetTimesPerWeek,
                        imageUri = imageUri
                    )
                )

                errorMessage.value = null
            } catch (exception: Exception) {
                errorMessage.value =
                    exception.message
                        ?: "Unable to update the habit."
            }
        }
    }

    fun deleteHabit(
        habit: HabitEntity
    ) {
        viewModelScope.launch {
            try {
                habitDao.deleteHabit(habit)
                errorMessage.value = null
            } catch (exception: Exception) {
                errorMessage.value =
                    exception.message
                        ?: "Unable to delete the habit."
            }
        }
    }

    fun toggleHabitForToday(
        habitId: Long
    ) {
        viewModelScope.launch {
            try {
                val date =
                    DateUtils.todayIso()

                val currentLog =
                    habitDao.getLogForDate(
                        habitId = habitId,
                        date = date
                    )

                val updatedLog =
                    if (currentLog == null) {
                        HabitLogEntity(
                            habitOwnerId = habitId,
                            logDate = date,
                            isCompleted = true
                        )
                    } else {
                        currentLog.copy(
                            isCompleted =
                                !currentLog.isCompleted
                        )
                    }

                habitDao.upsertHabitLog(updatedLog)

                errorMessage.value = null
            } catch (exception: Exception) {
                errorMessage.value =
                    exception.message
                        ?: "Unable to update today's progress."
            }
        }
    }

    fun setHabitActive(
        habit: HabitEntity,
        isActive: Boolean
    ) {
        viewModelScope.launch {
            try {
                habitDao.updateHabit(
                    habit.copy(
                        isActive = isActive
                    )
                )

                errorMessage.value = null
            } catch (exception: Exception) {
                errorMessage.value =
                    exception.message
                        ?: "Unable to update the habit."
            }
        }
    }

    fun clearError() {
        errorMessage.value = null
    }
}