package com.qcgm1978.forest.core.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import com.qcgm1978.forest.core.domain.model.Day
import com.qcgm1978.forest.core.domain.model.of
import com.qcgm1978.forest.core.domain.repository.DayRepository
import com.qcgm1978.forest.settings.domain.repository.SettingsRepository
import java.time.LocalDate

class GetDayImpl(
    private val dayRepository: DayRepository,
    private val settingsRepository: SettingsRepository,
) : GetDay {

    override fun invoke(date: LocalDate): Flow<Day> {
        val settingsFlow = settingsRepository.getSettings()
        val dayFlow = dayRepository.getDay(date)

        return settingsFlow.combine(dayFlow) { settings, day ->
            day ?: Day.of(date, settings, steps = 0)
        }
    }
}