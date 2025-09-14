package com.qcgm1978.forest.core.domain.usecase

import com.qcgm1978.forest.core.domain.repository.DayRepository
import com.qcgm1978.forest.settings.domain.repository.SettingsRepository

class DayUseCases(
    dayRepository: DayRepository,
    settingsRepository: SettingsRepository
) {

    val getDay: GetDay = GetDayImpl(dayRepository, settingsRepository)
    val incrementStepCount: IncrementStepCount = IncrementStepCountImpl(dayRepository, getDay)
}