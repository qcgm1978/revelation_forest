package com.qcgm1978.forest.progress

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.qcgm1978.forest.R
import com.qcgm1978.forest.databinding.FragmentProgressBinding
import kotlinx.coroutines.launch
import java.text.DecimalFormat

class ProgressFragment : Fragment() {

    private val viewModel: ProgressViewModel by activityViewModels { ProgressViewModel }

    private var _binding: FragmentProgressBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProgressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.progress.collect { progress -> updateUserInterface(progress) }
            }
        }
    }

    private fun updateUserInterface(state: ProgressState) {
        updateProgress(state)
        updateTree(state)
        updateTiles(state)
    }

    private fun updateProgress(state: ProgressState) = state.apply {
        val numberFormat = DecimalFormat.getIntegerInstance()
        val formattedStepCount = numberFormat.format(stepsTaken)
        val dailyGoalStepCount = numberFormat.format(dailyGoal)
        val dailyGoalText = getString(R.string.step_goal, dailyGoalStepCount)
        binding.apply {
            textStepCount.text = resources.getQuantityString(R.plurals.step_count_format, stepsTaken, stepsTaken)
            textDailyGoal.text = dailyGoalText
            progressDailyGoal.max = dailyGoal
            progressDailyGoal.progress = stepsTaken
        }
    }

    private fun updateTree(state: ProgressState) = state.apply {
        val progressPercent = if (dailyGoal > 0) (stepsTaken * 100 / dailyGoal) else 0
        val treeResource = getTreeResource(progressPercent)
        binding.imageTree.setImageResource(treeResource)
    }

    private fun updateTiles(state: ProgressState) = state.apply {
        val calorieText = getString(
            R.string.calorie_burned_format, calorieBurned
        )
        val distanceText = getString(
            R.string.distance_travelled_format, distanceTravelled
        )
        val carbonDioxideText = getString(
            R.string.carbon_dioxide_saved_format, carbonDioxideSaved
        )
        binding.apply {
            textCalorieBurned.text = calorieText
            textDistanceTravelled.text = distanceText
            textCarbonDioxideSaved.text = carbonDioxideText
        }
    }

    private fun getTreeResource(progress: Int): Int {
        return when (progress) {
            in 0..10 -> R.drawable.cover
            in 11..20 -> R.drawable.part_1
            in 21..30 -> R.drawable.part_2
            in 31..40 -> R.drawable.part_3
            in 41..50 -> R.drawable.part_4
            in 51..60 -> R.drawable.part_5
            in 61..70 -> R.drawable.part_6
            in 71..80 -> R.drawable.part_7
            else -> R.drawable.part_8
        }
    }
}