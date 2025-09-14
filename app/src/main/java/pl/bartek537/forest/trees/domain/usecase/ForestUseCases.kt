package com.qcgm1978.forest.trees.domain.usecase

import com.qcgm1978.forest.core.domain.repository.DayRepository

class ForestUseCases(
    dayRepository: DayRepository
) {

    val getTreeCount: GetTreeCount = GetTreeCountImpl(dayRepository)
}