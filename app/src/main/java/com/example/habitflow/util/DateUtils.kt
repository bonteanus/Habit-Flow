package com.example.habitflow.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {

    private const val ISO_DATE_PATTERN = "yyyy-MM-dd"

    fun todayIso(): String {
        return formatIsoDate(Date())
    }

    fun formatIsoDate(date: Date): String {
        return SimpleDateFormat(
            ISO_DATE_PATTERN,
            Locale.UK
        ).format(date)
    }

    /**
     * Returns the current Monday-to-Sunday date range.
     *
     * Example:
     * Pair("2026-07-13", "2026-07-19")
     */
    fun currentWeekRange(): Pair<String, String> {
        val calendar = Calendar.getInstance(Locale.UK)

        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val currentDay = calendar.get(Calendar.DAY_OF_WEEK)

        val daysSinceMonday =
            (currentDay - Calendar.MONDAY + 7) % 7

        calendar.add(
            Calendar.DAY_OF_MONTH,
            -daysSinceMonday
        )

        val monday = formatIsoDate(calendar.time)

        calendar.add(Calendar.DAY_OF_MONTH, 6)

        val sunday = formatIsoDate(calendar.time)

        return monday to sunday
    }
}