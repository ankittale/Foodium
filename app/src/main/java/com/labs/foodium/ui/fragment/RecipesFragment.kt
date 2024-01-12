package com.labs.foodium.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.labs.foodium.FoodViewModel
import com.labs.foodium.R
import com.labs.foodium.adapter.RecipesAdapter
import com.labs.foodium.databinding.FragmentRecipesBinding
import com.labs.foodium.utils.Constants
import com.labs.foodium.utils.NetworkResult
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecipesFragment: Fragment() {

    private lateinit var foodViewModel: FoodViewModel
    private val recipesAdapter by lazy { RecipesAdapter() }
    private lateinit var fragmentRecipesBinding: FragmentRecipesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        foodViewModel = ViewModelProvider(requireActivity())[FoodViewModel::class.java]
    }

    override fun onCreateView( inflater: LayoutInflater,  container: ViewGroup?, savedInstanceState: Bundle? ): View {
        fragmentRecipesBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipes, container, false)
        setupRecycler()
        requestApiData()
        return fragmentRecipesBinding.root
    }

    private fun setupRecycler() {
        fragmentRecipesBinding.recyclerRecipe.adapter = recipesAdapter
        fragmentRecipesBinding.recyclerRecipe.layoutManager = LinearLayoutManager(requireContext())
        showShimmerEffect()
    }

    private fun showShimmerEffect() {
        fragmentRecipesBinding.shimmerFrameView.showShimmer(true)
    }

    private fun requestApiData() {
        foodViewModel.getRecipes(applyQueries())
        foodViewModel.recipeResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    hideShimmerEffect()
                    response.data?.let { recipesAdapter.setData(it) }
                }

                is NetworkResult.Error -> {
                    hideShimmerEffect()
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

    private fun  applyQueries(): HashMap<String, String>{
        val queries : HashMap<String, String> = HashMap()
        queries[Constants.QUERY_NUMBER] = "50"
        queries[Constants.QUERY_API_KEY] = Constants.API_KEY
        queries[Constants.QUERY_TYPE] = "snack"
        queries[Constants.QUERY_DIET] = "vegan"
        queries[Constants.QUERY_ADD_RECIPE_INFORMATION] = "true"
        queries[Constants.QUERY_FILL_INGREDIENTS] = "true"
        return queries
    }

    private fun hideShimmerEffect() {
        fragmentRecipesBinding.shimmerFrameView.hideShimmer()
        fragmentRecipesBinding.shimmerFrameView.visibility = View.GONE
    }
}