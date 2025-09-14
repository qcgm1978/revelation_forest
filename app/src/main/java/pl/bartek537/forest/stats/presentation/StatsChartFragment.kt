package com.qcgm1978.forest.stats.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.qcgm1978.forest.R
import com.qcgm1978.forest.databinding.FragmentStatsChartBinding
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

class StatsChartFragment : Fragment() {

    private val statsDetailsViewModel: StatsDetailsViewModel by activityViewModels { StatsDetailsViewModel.Factory }

    private lateinit var binding: FragmentStatsChartBinding
    private lateinit var chartPageAdapter: ChartPageAdapter

    private val dateFormatter = DateTimeFormatter.ofPattern("EEE, MMM dd")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentStatsChartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chartPageAdapter = ChartPageAdapter(this)
        binding.viewPagerChart.adapter = chartPageAdapter

        binding.buttonPreviousDay.setOnClickListener { changeSelectedDate(offset = -1) }
        binding.buttonNextDay.setOnClickListener { changeSelectedDate(offset = 1) }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                statsDetailsViewModel.day.collect { day ->
                    val progress = if (day.goal > 0) day.stepsTaken * 100 / day.goal else 0
                    updateUserInterface(day.date, day.chartDateRange, progress)
                }
            }
        }
    }

    private fun changeSelectedDate(offset: Long) {
        val currentDate = statsDetailsViewModel.day.value.date
        statsDetailsViewModel.selectDay(currentDate.plusDays(offset))
    }

    private fun updateUserInterface(selectedDate: LocalDate, dateRange: ClosedRange<LocalDate>, progress: Int) {
        binding.apply {
            textSelectedDate.text = selectedDate.format(dateFormatter)
            buttonPreviousDay.isVisible = selectedDate.isAfter(dateRange.start)
            buttonNextDay.isVisible = selectedDate.isBefore(dateRange.endInclusive)
            chartPageAdapter.dateRange = dateRange
            scrollChartTo(selectedDate)
            imageTree.setImageResource(getTreeResource(progress))
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

    private fun scrollChartTo(
        selectedDate: LocalDate,
    ) {
        val pageIndex = chartPageAdapter.getPageContaining(selectedDate)
        binding.viewPagerChart.currentItem = pageIndex
    }

    class ChartPageAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        var dateRange = LocalDate.now()..LocalDate.now()

        fun getPageContaining(selectedDate: LocalDate): Int {
            val period = Period.between(selectedDate, dateRange.endInclusive)
            return (period.days / 7).coerceIn(0, itemCount)
        }

        override fun getItemCount(): Int = dateRange.run {
            val period = Period.between(start, endInclusive)
            return period.days / 7 + 1
        }

        override fun createFragment(position: Int): Fragment {
            val fragment = StatsChartPageFragment()
            fragment.arguments = Bundle().apply {
                putLong(StatsChartPageFragment.ARG_PAGE_NUMBER, position.toLong())
            }
            return fragment
        }
    }
}
