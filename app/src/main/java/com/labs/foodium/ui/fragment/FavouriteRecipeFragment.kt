package com.labs.foodium.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.labs.foodium.R
import com.labs.foodium.adapter.FavoriteRecipeAdapter
import com.labs.foodium.databinding.FragmentFavouriteBinding
import com.labs.foodium.viewmodel.FoodViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavouriteRecipeFragment: Fragment() {

    private val favoriteAdapter: FavoriteRecipeAdapter by lazy { FavoriteRecipeAdapter(requireActivity()) }
    private val foodViewModel: FoodViewModel by viewModels()
    private var  _binding: FragmentFavouriteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView( inflater: LayoutInflater,
                               container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favourite, container, false)
        binding.lifecycleOwner = this
        binding.foodViewModel = foodViewModel
        setupRecyclerView()
        foodViewModel.readFavouriteRecipes.observe(viewLifecycleOwner) { favoriteEntity ->
            favoriteAdapter.setData(favoriteEntity)
        }
        return binding.root
    }

    private fun setupRecyclerView() {
        binding.favoriteRecipesRecyclerView.adapter = favoriteAdapter
        binding.favoriteRecipesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}