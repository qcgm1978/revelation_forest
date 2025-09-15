package com.qcgm1978.forest.service

import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionEvent
import com.google.android.gms.location.DetectedActivity
import com.qcgm1978.forest.ForestApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.qcgm1978.forest.core.domain.usecase.DayUseCases
import java.time.LocalDate
import kotlin.math.roundToInt

class StepCounterController(
    private val dayUseCases: DayUseCases,
    private val coroutineScope: CoroutineScope,
    private val application: ForestApplication,
) {

    private val _stats = MutableStateFlow(StepCounterState(LocalDate.now(), 0, 0, 0.0, 0))
    val stats: StateFlow<StepCounterState> = _stats.asStateFlow()

    private var getStatsJob: Job? = null

    init {
        coroutineScope.launch {
            application.currentDate.collect { getStats(it) }
        }
    }

    private fun getStats(date: LocalDate) {
        getStatsJob?.cancel()

        getStatsJob = dayUseCases.getDay(date).onEach { day ->
            _stats.value = day.run {
                StepCounterState(
                    date = date,
                    steps = steps,
                    goal = goal,
                    distanceTravelled = distanceTravelled,
                    calorieBurned = calorieBurned.roundToInt()
                )
            }
            application.steps.value = day.steps
        }.launchIn(coroutineScope)
    }

    private val rawStepSensorReadings = MutableStateFlow(StepCounterEvent(0, LocalDate.MIN))
    private var previousStepCount: Int? = null

    init {
        rawStepSensorReadings.drop(1).onEach { event ->
            val stepCountDifference = event.stepCount - (previousStepCount ?: event.stepCount)
            previousStepCount = event.stepCount
            dayUseCases.incrementStepCount(event.eventDate, stepCountDifference)
        }.launchIn(coroutineScope)
    }

    fun onStepCountChanged(newStepCount: Int, eventDate: LocalDate) {
        application.steps.value = newStepCount
        rawStepSensorReadings.value = StepCounterEvent(newStepCount, eventDate)
    }

    private var stillActivityStartTime: Long? = null

    fun onActivityTransition(event: ActivityTransitionEvent) {
        if (event.activityType == DetectedActivity.STILL) {
            if (event.transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER) {
                stillActivityStartTime = System.currentTimeMillis()
            } else if (event.transitionType == ActivityTransition.ACTIVITY_TRANSITION_EXIT) {
                stillActivityStartTime?.let { startTime ->
                    val duration = System.currentTimeMillis() - startTime
                    coroutineScope.launch {
                        dayUseCases.incrementStandTime(stats.value.date, duration)
                    }
                    stillActivityStartTime = null
                }
            }
        }
    }
}