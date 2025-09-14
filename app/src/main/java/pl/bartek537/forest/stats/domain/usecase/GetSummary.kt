package com.qcgm1978.forest.stats.domain.usecase

import com.qcgm1978.forest.core.domain.model.StatsSummary
import com.qcgm1978.forest.core.domain.model.of
import com.qcgm1978.forest.core.domain.repository.DayRepository

interface GetSummary {
    suspend operator fun invoke(): StatsSummary
}

class GetSummaryImpl(
    private val dayRepository: DayRepository
) : GetSummary {

    override suspend operator fun invoke(): StatsSummary {
        val allDays = dayRepository.getAllDays()
        return StatsSummary.of(allDays)
    }
}