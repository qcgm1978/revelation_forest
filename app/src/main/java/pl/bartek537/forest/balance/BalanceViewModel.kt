package com.qcgm1978.forest.balance

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.pow
import kotlin.math.sqrt

class BalanceViewModel : ViewModel() {

    private val _balanceScore = MutableStateFlow(100.0)
    val balanceScore: StateFlow<Double> = _balanceScore

    private var isFirstEvent = true

    fun onSensorDataChanged(x: Float, y: Float, z: Float) {
        if (isFirstEvent) {
            isFirstEvent = false
            return
        }

        val acceleration = sqrt(x.toDouble().pow(2) + y.toDouble().pow(2) + z.toDouble().pow(2))

        var currentScore = _balanceScore.value

        // Decrease score based on linear acceleration, sensitivity tuned down.
        currentScore -= acceleration * 2.0

        // Gradually restore the score if not shaking.
        if (acceleration < 0.2) {
            currentScore += 0.5
        }

        _balanceScore.value = currentScore.coerceIn(0.0, 100.0)
    }

    fun reset() {
        isFirstEvent = true
        _balanceScore.value = 100.0
    }
}