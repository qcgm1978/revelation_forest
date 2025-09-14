package com.qcgm1978.forest.core.domain.usecase

import kotlinx.coroutines.flow.Flow
import com.qcgm1978.forest.core.domain.model.Day
import java.time.LocalDate

interface GetDay {

    operator fun invoke(date: LocalDate): Flow<Day>
}