package com.qcgm1978.forest.settings.domain.usecase

import com.qcgm1978.forest.core.domain.repository.DayRepository
import com.qcgm1978.forest.settings.domain.repository.SettingsRepository

class SettingsUseCases(
    settingsRepository: SettingsRepository,
    dayRepository: DayRepository,
) {

    val getSettings: GetSettings = GetSettingsImpl(settingsRepository)
    val updateDaySettings: UpdateDaySettings = UpdateDaySettingsImpl(dayRepository)
}