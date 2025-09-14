package com.qcgm1978.forest.stats.domain.usecase

import com.qcgm1978.forest.core.domain.repository.DayRepository

class StatsChartPageUseCases(
    dayRepository: DayRepository
) {

    val getWeek: GetWeek = GetWeekImpl(dayRepository)
}