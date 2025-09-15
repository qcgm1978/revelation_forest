package com.qcgm1978.forest.standtime

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.qcgm1978.forest.databinding.FragmentStandTimeBinding
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class StandTimeFragment : Fragment() {

    private var _binding: FragmentStandTimeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StandTimeViewModel by viewModels { StandTimeViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStandTimeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonStartStill.setOnClickListener {
            viewModel.startStill()
        }

        binding.buttonStopStill.setOnClickListener {
            viewModel.stopStill()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.standTime.collect { standTimeInMillis ->
                    val hours = TimeUnit.MILLISECONDS.toHours(standTimeInMillis)
                    val minutes = TimeUnit.MILLISECONDS.toMinutes(standTimeInMillis) % 60
                    val seconds = TimeUnit.MILLISECONDS.toSeconds(standTimeInMillis) % 60
                    binding.textStandTime.text = String.format("%d h %02d m %02d s", hours, minutes, seconds)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}