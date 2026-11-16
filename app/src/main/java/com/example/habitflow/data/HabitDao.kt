package com.example.habitflow.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {

    // -------------------------
    // Habit operations
    // -------------------------

    @Query(
        """
        SELECT * FROM habits
        ORDER BY isActive DESC, name COLLATE NOCASE ASC
        """
    )
    fun observeAllHabits(): Flow<List<HabitEntity>>

    @Query(
        """
        SELECT * FROM habits
        WHERE habitId = :habitId
        LIMIT 1
        """
    )
    fun observeHabitById(habitId: Long): Flow<HabitEntity?>

    @Insert
    suspend fun insertHabit(habit: HabitEntity): Long

    @Update
    suspend fun updateHabit(habit: HabitEntity)

    @Delete
    suspend fun deleteHabit(habit: HabitEntity)

    @Query(
        """
        DELETE FROM habits
        WHERE habitId = :habitId
        """
    )
    suspend fun deleteHabitById(habitId: Long)

    // -------------------------
    // Daily log operations
    // -------------------------

    @Query(
        """
        SELECT * FROM habit_logs
        WHERE habitOwnerId = :habitId
        ORDER BY logDate DESC
        """
    )
    fun observeLogsForHabit(
        habitId: Long
    ): Flow<List<HabitLogEntity>>

    @Query(
        """
        SELECT * FROM habit_logs
        WHERE logDate = :date
        ORDER BY habitOwnerId ASC
        """
    )
    fun observeLogsForDate(
        date: String
    ): Flow<List<HabitLogEntity>>

    @Query(
        """
        SELECT * FROM habit_logs
        WHERE habitOwnerId = :habitId
        AND logDate = :date
        LIMIT 1
        """
    )
    suspend fun getLogForDate(
        habitId: Long,
        date: String
    ): HabitLogEntity?

    /*
     * Inserts a new daily log or updates the existing log for the
     * same habit and date.
     */
    @Upsert
    suspend fun upsertHabitLog(log: HabitLogEntity)

    @Query(
        """
        DELETE FROM habit_logs
        WHERE habitOwnerId = :habitId
        AND logDate = :date
        """
    )
    suspend fun deleteLogForDate(
        habitId: Long,
        date: String
    )

    @Query(
        """
        SELECT * FROM habit_logs
        WHERE logDate BETWEEN :startDate AND :endDate
        ORDER BY logDate ASC
        """
    )
    fun observeLogsBetweenDates(
        startDate: String,
        endDate: String
    ): Flow<List<HabitLogEntity>>

    @Query(
        """
        SELECT COUNT(*) FROM habit_logs
        WHERE habitOwnerId = :habitId
        AND isCompleted = 1
        AND logDate BETWEEN :startDate AND :endDate
        """
    )
    fun observeCompletionCount(
        habitId: Long,
        startDate: String,
        endDate: String
    ): Flow<Int>
}