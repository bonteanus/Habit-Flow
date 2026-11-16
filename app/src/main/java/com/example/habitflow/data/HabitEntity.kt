package com.example.habitflow.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true)
    val habitId: Long = 0,

    val name: String,

    val category: String,

    /*
     * Stored as yyyy-MM-dd.
     * Example: 2026-07-16
     */
    val startDate: String,

    /*
     * Number of times the habit should be completed each week.
     * Daily habits will normally use 7.
     */
    val targetTimesPerWeek: Int,

    /*
     * Optional URI for a photograph attached to the habit.
     */
    val imageUri: String? = null,

    val isActive: Boolean = true
)