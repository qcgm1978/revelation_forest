package com.qcgm1978.forest.core.domain.repository

import kotlinx.coroutines.flow.Flow
import com.qcgm1978.forest.core.domain.model.Day
import com.qcgm1978.forest.core.domain.model.DaySettings
import java.time.LocalDate

interface DayRepository {

    fun getTreeCount(): Flow<Int>

    fun getFirstDay(): Flow<Day?>

    fun getDay(date: LocalDate): Flow<Day?>

    suspend fun getAllDays(): List<Day>

    fun getDays(range: ClosedRange<LocalDate>): Flow<List<Day>>

    suspend fun upsertDay(day: Day)

    suspend fun updateDaySettings(daySettings: DaySettings)

    suspend fun incrementStandTime(date: LocalDate, duration: Long)
}