package com.qcgm1978.forest.settings.data.source

import kotlinx.coroutines.flow.Flow
import com.qcgm1978.forest.settings.domain.model.Settings

interface SettingsStore {

    fun getSettings(): Flow<Settings>
}