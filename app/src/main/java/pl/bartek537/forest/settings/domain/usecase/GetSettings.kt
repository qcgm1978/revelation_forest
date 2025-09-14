package com.qcgm1978.forest.settings.domain.usecase

import kotlinx.coroutines.flow.Flow
import com.qcgm1978.forest.settings.domain.model.Settings
import com.qcgm1978.forest.settings.domain.repository.SettingsRepository

interface GetSettings {

    operator fun invoke(): Flow<Settings>
}

class GetSettingsImpl(
    private val repository: SettingsRepository
) : GetSettings {

    override fun invoke(): Flow<Settings> {
        return repository.getSettings()
    }
}