package com.qcgm1978.forest.settings.data.repository

import kotlinx.coroutines.flow.Flow
import com.qcgm1978.forest.settings.data.source.SettingsStore
import com.qcgm1978.forest.settings.domain.model.Settings
import com.qcgm1978.forest.settings.domain.repository.SettingsRepository

class SettingsRepositoryImpl(
    private val settingsStore: SettingsStore
) : SettingsRepository {

    override fun getSettings(): Flow<Settings> {
        return settingsStore.getSettings()
    }
}