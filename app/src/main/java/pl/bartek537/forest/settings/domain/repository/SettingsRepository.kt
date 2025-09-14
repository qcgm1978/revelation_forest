package com.qcgm1978.forest.settings.domain.repository

import kotlinx.coroutines.flow.Flow
import com.qcgm1978.forest.settings.domain.model.Settings

interface SettingsRepository {

    fun getSettings(): Flow<Settings>
}