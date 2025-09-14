package com.qcgm1978.forest.core.domain.usecase

import java.time.LocalDate

interface IncrementStepCount {

    suspend operator fun invoke(date: LocalDate, by: Int)
}