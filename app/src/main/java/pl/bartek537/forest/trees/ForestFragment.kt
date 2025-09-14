package com.qcgm1978.forest.trees

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.qcgm1978.forest.BuildConfig
import com.qcgm1978.forest.R
import com.qcgm1978.forest.databinding.FragmentForestBinding
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ForestFragment : Fragment() {

    private val viewModel: ForestViewModel by viewModels { ForestViewModel.Factory }

    private lateinit var binding: FragmentForestBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentForestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupDebugButtons()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.flowerState.collect { state ->
                    updateUi(state)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.flowerState.map { it.flowerStage }.distinctUntilChanged().collect { _ ->
                    animateFlowerBloom()
                }
            }
        }
    }

    private fun setupDebugButtons() {
        if (BuildConfig.DEBUG) {
            binding.buttonWalk.isVisible = true
            binding.buttonReset.isVisible = true

            binding.buttonWalk.setOnClickListener {
                viewModel.incrementSteps(500)
            }

            binding.buttonReset.setOnClickListener {
                viewModel.resetSteps()
            }
        }
    }

    private fun updateUi(state: FlowerState) {
        binding.textStepCount.text = resources.getQuantityString(R.plurals.step_count_format, state.steps, state.steps)
        binding.imageFlower.text = state.flowerIcon

        val scale = 0.25f + (state.steps % 3000) / 3000f * 0.75f
        binding.imageFlower.scaleX = scale
        binding.imageFlower.scaleY = scale
    }

    private fun animateFlowerBloom() {
        val animator = ObjectAnimator.ofFloat(binding.imageFlower, "alpha", 0f, 1f).apply {
            duration = 1000
        }
        animator.start()
    }
}
