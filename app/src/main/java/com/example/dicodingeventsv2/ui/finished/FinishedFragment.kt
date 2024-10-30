package com.example.dicodingeventsv2.ui.finished

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicodingeventsv2.adapter.EventAdapter
import com.example.dicodingeventsv2.databinding.FragmentFinishedBinding
import com.example.dicodingeventsv2.ui.viewModels.MainViewModel
import com.example.dicodingeventsv2.ui.viewModels.ViewModelFactory
import com.example.dicodingeventsv2.utils.Result

class FinishedFragment : Fragment() {
    private var _binding: FragmentFinishedBinding? = null
    private val binding get() = _binding!!
    private lateinit var eventAdapter: EventAdapter
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinishedBinding.inflate(inflater, container, false)
        val root: View = binding.root

        eventAdapter = EventAdapter(EventAdapter.VIEW_TYPE_FINISHED_AT_HOME)
        binding.finishedEvent.layoutManager = LinearLayoutManager(context)
        binding.finishedEvent.adapter = eventAdapter

        with(binding) {
            searchView.setupWithSearchBar(searchBar)
            searchView
                .editText
                .setOnEditorActionListener { _, _, _ ->
                    val query = searchView.text.toString()
                    searchBar.setText(query)
                    searchView.hide()
                    viewModel.searchEvent(query).observe(viewLifecycleOwner) { event ->
                        when (event) {
                            is Result.Loading -> {
                                binding.progressBar1.visibility = View.VISIBLE
                            }

                            is Result.Success -> {
                                binding.progressBar1.visibility = View.GONE
                                eventAdapter.submitList(event.data)
                            }

                            is Result.Error -> {
                                binding.apply {
                                    errorMessage.text = event.error
                                    errorMessage.visibility = View.VISIBLE
                                    titleFinished.visibility = View.GONE
                                    progressBar1.visibility = View.GONE
                                    searchBar.visibility = View.GONE
                                    searchView.visibility = View.GONE
                                    finishedEvent.visibility = View.GONE
                                }
                            }
                        }
                        Log.d("eventDataSearch", event.toString())
                    }
                    false
                }
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getFinishedEvent().observe(viewLifecycleOwner) { event ->
            when (event) {
                is Result.Loading -> {
                    binding.progressBar1.visibility = View.VISIBLE
                }

                is Result.Success -> {
                    binding.progressBar1.visibility = View.GONE
                    eventAdapter.submitList(event.data)
                }

                is Result.Error -> {
                    binding.apply {
                        errorMessage.text = event.error
                        errorMessage.visibility = View.VISIBLE
                        titleFinished.visibility = View.GONE
                        progressBar1.visibility = View.GONE
                        searchBar.visibility = View.GONE
                        searchView.visibility = View.GONE
                        finishedEvent.visibility = View.GONE
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