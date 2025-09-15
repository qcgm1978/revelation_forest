package com.qcgm1978.forest.core.domain.usecase

import com.qcgm1978.forest.core.domain.repository.DayRepository
import java.time.LocalDate

interface IncrementStandTime {
    suspend operator fun invoke(date: LocalDate, duration: Long)
}

class IncrementStandTimeImpl(
    private val dayRepository: DayRepository
) : IncrementStandTime {
    override suspend fun invoke(date: LocalDate, duration: Long) {
        dayRepository.incrementStandTime(date, duration)
    }
}