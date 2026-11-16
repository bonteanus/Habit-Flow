package com.example.habitflow.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "habit_logs",
    foreignKeys = [
        ForeignKey(
            entity = HabitEntity::class,
            parentColumns = ["habitId"],
            childColumns = ["habitOwnerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["habitOwnerId"]),

        /*
         * A habit can only have one log for a particular date.
         */
        Index(
            value = ["habitOwnerId", "logDate"],
            unique = true
        )
    ]
)
data class HabitLogEntity(
    @PrimaryKey(autoGenerate = true)
    val logId: Long = 0,

    val habitOwnerId: Long,

    /*
     * Stored as yyyy-MM-dd.
     */
    val logDate: String,

    val isCompleted: Boolean,

    /*
     * Optional evidence photograph for this completion.
     */
    val photoUri: String? = null
)