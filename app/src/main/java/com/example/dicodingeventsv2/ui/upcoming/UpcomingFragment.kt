package com.example.dicodingeventsv2.ui.upcoming

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicodingeventsv2.adapter.EventAdapter
import com.example.dicodingeventsv2.databinding.FragmentUpcomingBinding
import com.example.dicodingeventsv2.ui.viewModels.MainViewModel
import com.example.dicodingeventsv2.ui.viewModels.ViewModelFactory
import com.example.dicodingeventsv2.utils.Result

class UpcomingFragment : Fragment() {
    private var _binding: FragmentUpcomingBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: EventAdapter
    private val viewModel by viewModels<MainViewModel>{
        ViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpcomingBinding.inflate(inflater, container, false)
        val root = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = EventAdapter(EventAdapter.VIEW_TYPE_UPCOMING_AT_HOME)
        binding.upcomingEvent.layoutManager = LinearLayoutManager(context)
        binding.upcomingEvent.adapter = adapter

        viewModel.getUpcomingEvent().observe(viewLifecycleOwner) {event ->
            when(event) {
                is Result.Loading -> {
                    binding.progressBar1.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding.progressBar1.visibility = View.GONE
                    adapter.submitList(event.data)
                }
                is Result.Error -> {
                    binding.apply {
                        errorMessage.text = event.error
                        errorMessage.visibility = View.VISIBLE
                        titleUpcoming.visibility = View.GONE
                        upcomingEvent.visibility = View.GONE
                        progressBar1.visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}