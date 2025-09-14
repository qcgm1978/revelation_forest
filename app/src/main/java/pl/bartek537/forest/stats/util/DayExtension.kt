package com.qcgm1978.forest.stats.util

import com.qcgm1978.forest.core.domain.model.Day
import java.time.LocalDate

fun List<Day>.alignWeek(
    firstDay: LocalDate,
    lastDay: LocalDate = firstDay.plusDays(6),
): List<Day> {
    val alignedWeek = mutableListOf<Day>()
    for (date in firstDay..lastDay) {
        val currentDay = singleOrNull { it.date == date }
        alignedWeek.add(currentDay ?: Day(date, goal = 0))
    }
    return alignedWeek
}