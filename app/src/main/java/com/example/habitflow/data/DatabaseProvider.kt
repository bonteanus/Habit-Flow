package com.example.habitflow.data

import android.content.Context
import androidx.room.Room

object DatabaseProvider {

    @Volatile
    private var databaseInstance: HabitDatabase? = null

    fun getDatabase(context: Context): HabitDatabase {
        return databaseInstance ?: synchronized(this) {

            val newInstance = Room.databaseBuilder(
                context.applicationContext,
                HabitDatabase::class.java,
                "habitflow_database"
            ).build()

            databaseInstance = newInstance
            newInstance
        }
    }
}