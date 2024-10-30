package com.example.dicodingeventsv2.ui.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicodingeventsv2.adapter.EventAdapter
import com.example.dicodingeventsv2.databinding.FragmentFavoriteBinding
import com.example.dicodingeventsv2.ui.viewModels.MainViewModel
import com.example.dicodingeventsv2.ui.viewModels.ViewModelFactory

class FavoriteFragment : Fragment() {
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private val adapter by lazy { EventAdapter(EventAdapter.VIEW_TYPE_UPCOMING_AT_HOME) }
    private val viewModel by viewModels<MainViewModel> { ViewModelFactory.getInstance(requireActivity()) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentFavoriteBinding.inflate(inflater, container, false).also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.favorite.apply {
            adapter = this@FavoriteFragment.adapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }
        observeFavorites()
    }

    private fun observeFavorites() {
        viewModel.getFavoriteEvent().observe(viewLifecycleOwner) { favEvent ->
            binding.apply {
                progressBar1.visibility = View.GONE
                favoriteCount.text = "Total: ${favEvent.size} Event"
                favorite.visibility = if (favEvent.isEmpty()) View.GONE else View.VISIBLE
                favoriteCount.visibility = favorite.visibility
                adapter.submitList(favEvent)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}