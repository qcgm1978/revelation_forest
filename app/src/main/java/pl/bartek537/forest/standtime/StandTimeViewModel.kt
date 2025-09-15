package com.qcgm1978.forest.standtime

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.qcgm1978.forest.ForestApplication
import com.qcgm1978.forest.core.data.repository.DayRepositoryImpl
import com.qcgm1978.forest.core.domain.usecase.DayUseCases
import com.qcgm1978.forest.settings.data.repository.SettingsRepositoryImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class StandTimeViewModel(
    private val dayUseCases: DayUseCases,
    private val currentDateFlow: StateFlow<LocalDate>
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val standTime: StateFlow<Long> = currentDateFlow
        .flatMapLatest { date ->
            dayUseCases.getDay(date)
        }
        .map { it?.standTime ?: 0L }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    private var debugStillStartTime: Long? = null

    fun startStill() {
        debugStillStartTime = System.currentTimeMillis()
    }

    fun stopStill() {
        debugStillStartTime?.let { startTime ->
            val duration = System.currentTimeMillis() - startTime
            viewModelScope.launch {
                dayUseCases.incrementStandTime(currentDateFlow.value, duration)
            }
            debugStillStartTime = null
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[APPLICATION_KEY]) as ForestApplication
                val dayDatabase = application.forestDatabase
                val dayRepository = DayRepositoryImpl(dayDatabase.dayDao)
                val settingsStore = application.settingsStore
                val settingsRepository = SettingsRepositoryImpl(settingsStore)
                val dayUseCases = DayUseCases(dayRepository, settingsRepository)

                return StandTimeViewModel(dayUseCases, application.currentDate) as T
            }
        }
    }
}