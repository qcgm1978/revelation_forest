package com.qcgm1978.forest.trees

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.qcgm1978.forest.ForestApplication
import com.qcgm1978.forest.R
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
    val flowerStage: Int = 1,
    val flowerName: String = ""
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
                val flowerName = getFlowerName(application, flowerStage)
                _flowerState.value = FlowerState(
                    steps = steps,
                    flowerStage = flowerStage,
                    flowerIcon = flowerIcon,
                    flowerName = flowerName
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
            steps < 30000 -> 5
            steps < 40000 -> 6
            steps < 50000 -> 7
            steps < 60000 -> 8
            steps < 70000 -> 9
            steps < 80000 -> 10
            else -> 11
        }
    }

    private fun getFlowerIcon(stage: Int): String {
        return when (stage) {
            1 -> "🌱" // 花苞
            2 -> "✨" // 满天星
            3 -> "🌿" // 数字薄荷
            4 -> "🌷" // 郁金香
            5 -> "🏵️" // 秋菊
            6 -> "🌺" // 火鹤花
            7 -> "💜" // 薰衣草
            8 -> "🌸" // 月光花
            9 -> "🌹" // 晚香玉
            10 -> "💐" // 白玫瑰
            else -> "🌻" // 完成！（向日葵）
        }
    }

    private fun getFlowerName(context: Context, stage: Int): String {
        val resourceId = context.resources.getIdentifier(
            "flower_name_$stage",
            "string",
            context.packageName
        )
        return context.getString(resourceId)
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
