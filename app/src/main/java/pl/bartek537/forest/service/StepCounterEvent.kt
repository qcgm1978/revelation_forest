package com.qcgm1978.forest.service

import java.time.LocalDate

data class StepCounterEvent(
    val stepCount: Int,
    val eventDate: LocalDate,
)