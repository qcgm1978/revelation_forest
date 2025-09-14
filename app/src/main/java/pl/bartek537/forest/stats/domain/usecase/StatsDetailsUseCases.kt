package com.qcgm1978.forest.stats.domain.usecase

import com.qcgm1978.forest.core.domain.repository.DayRepository

class StatsDetailsUseCases(
    dayRepository: DayRepository
) {

    val getFirstDate: GetFirstDate = GetFirstDateImpl(dayRepository)
}