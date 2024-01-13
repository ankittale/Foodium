package com.labs.foodium.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.labs.foodium.viewmodel.FoodViewModel
import com.labs.foodium.R
import com.labs.foodium.adapter.RecipesAdapter
import com.labs.foodium.databinding.FragmentRecipesBinding
import com.labs.foodium.utils.Constants
import com.labs.foodium.utils.NetworkResult
import com.labs.foodium.utils.observeOnce
import com.labs.foodium.viewmodel.RecipesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecipesFragment: Fragment() {

    private lateinit var foodViewModel: FoodViewModel
    private lateinit var recipesViewModel: RecipesViewModel
    private val recipesAdapter by lazy { RecipesAdapter() }
    private var _binding: FragmentRecipesBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<RecipesFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        foodViewModel = ViewModelProvider(requireActivity())[FoodViewModel::class.java]
        recipesViewModel = ViewModelProvider(requireActivity())[RecipesViewModel::class.java]
    }

    override fun onCreateView( inflater: LayoutInflater,  container: ViewGroup?, savedInstanceState: Bundle? ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipes, container, false)
        binding.lifecycleOwner = this // bcoz live data is use
        binding.foodViewModel = foodViewModel

        setupRecycler()
        readFromDatabase()

        binding.fabRecipe.setOnClickListener {
            findNavController().navigate(R.id.action_recipesFragment_to_recipesBottomSheet)
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupRecycler() {
        binding.recyclerRecipe.adapter = recipesAdapter
        binding.recyclerRecipe.layoutManager = LinearLayoutManager(requireContext())
        showShimmerEffect()
    }

    private fun readFromDatabase() {
        lifecycleScope.launch {
            foodViewModel.readRecipe.observeOnce(viewLifecycleOwner){ database ->
                if (database.isNotEmpty() && !args.backFromBottomSheet) {
                    Log.d("RecipeFragment", "From Database")
                    recipesAdapter.setData(database[0].foodRecipe)
                    hideShimmerEffect()
                } else {
                    requestApiData()
                }
            }
        }
    }

    private fun requestApiData() {
        Log.d("RecipeFragment", "From API call")
        foodViewModel.getRecipes(recipesViewModel.applyQueries())
        foodViewModel.recipeResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    hideShimmerEffect()
                    response.data?.let { recipesAdapter.setData(it) }
                }

                is NetworkResult.Error -> {
                    hideShimmerEffect()
                    loadDataFromCache()
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is NetworkResult.Loading -> {
                    showShimmerEffect()
                }

                else -> {}
            }
        }
    }

    private fun loadDataFromCache() {
        lifecycleScope.launch {
            foodViewModel.readRecipe.observe(viewLifecycleOwner){ database ->
                if (database.isNotEmpty()) {
                    recipesAdapter.setData(database[0].foodRecipe)
                }

            }
        }
    }

    private fun showShimmerEffect() {
        binding.shimmerFrameView.showShimmer(true)
    }

    private fun hideShimmerEffect() {
        binding.shimmerFrameView.hideShimmer()
        binding.shimmerFrameView.visibility = View.GONE
    }
}