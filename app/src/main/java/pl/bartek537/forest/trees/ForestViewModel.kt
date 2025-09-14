package com.qcgm1978.forest.trees

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.qcgm1978.forest.ForestApplication
import com.qcgm1978.forest.core.data.repository.DayRepositoryImpl
import com.qcgm1978.forest.core.domain.usecase.DayUseCases
import com.qcgm1978.forest.settings.data.repository.SettingsRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class FlowerState(
    val steps: Int = 0,
    val flowerIcon: String = "🌱",
    val flowerStage: Int = 1
)

class ForestViewModel(
    private val dayUseCases: DayUseCases,
    private val application: ForestApplication,
) : ViewModel() {

    private val _flowerState = MutableStateFlow(FlowerState())
    val flowerState: StateFlow<FlowerState> = _flowerState.asStateFlow()

    init {
        viewModelScope.launch {
            application.steps.collect { steps ->
                val flowerStage = getFlowerStage(steps)
                val flowerIcon = getFlowerIcon(flowerStage)
                _flowerState.value = FlowerState(
                    steps = steps,
                    flowerStage = flowerStage,
                    flowerIcon = flowerIcon
                )
            }
        }
    }

    fun incrementSteps(amount: Int) {
        viewModelScope.launch {
            dayUseCases.incrementStepCount(application.currentDate.value, amount)
        }
    }

    fun resetSteps() {
        viewModelScope.launch {
            val day = dayUseCases.getDay(application.currentDate.value).first()
            dayUseCases.incrementStepCount(application.currentDate.value, -day.steps)
        }
    }

    private fun getFlowerStage(steps: Int): Int {
        return when {
            steps < 3000 -> 1
            steps < 6000 -> 2
            steps < 10000 -> 3
            steps < 20000 -> 4
            else -> 5
        }
    }

    private fun getFlowerIcon(stage: Int): String {
        return when (stage) {
            1 -> "🌱"
            2 -> "✨"
            3 -> "🌿"
            4 -> "🏵️"
            else -> "✅"
        }
    }

    object Factory : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            val application = checkNotNull(extras[APPLICATION_KEY]) as ForestApplication
            val dayDatabase = application.forestDatabase
            val dayRepository = DayRepositoryImpl(dayDatabase.dayDao)
            val settingsStore = application.settingsStore
            val settingsRepository = SettingsRepositoryImpl(settingsStore)
            val dayUseCases = DayUseCases(dayRepository, settingsRepository)
            return ForestViewModel(dayUseCases, application) as T
        }
    }
}
