package com.qcgm1978.forest.stats.domain.usecase

import com.qcgm1978.forest.core.domain.repository.DayRepository

class StatsSummaryUseCases(
    dayRepository: DayRepository
) {

    val getSummary: GetSummary = GetSummaryImpl(dayRepository)
}