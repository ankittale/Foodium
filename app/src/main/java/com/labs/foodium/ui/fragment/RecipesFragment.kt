package com.labs.foodium.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.labs.foodium.viewmodel.FoodViewModel
import com.labs.foodium.R
import com.labs.foodium.adapter.RecipesAdapter
import com.labs.foodium.databinding.FragmentRecipesBinding
import com.labs.foodium.utils.NetworkListener
import com.labs.foodium.utils.NetworkResult
import com.labs.foodium.utils.observeOnce
import com.labs.foodium.viewmodel.RecipesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecipesFragment: Fragment() {

    private var isDataRequested = false
    private val args by navArgs<RecipesFragmentArgs>()

    private lateinit var foodViewModel: FoodViewModel
    private lateinit var recipesViewModel: RecipesViewModel

    private val recipesAdapter by lazy { RecipesAdapter() }
    private var _binding: FragmentRecipesBinding? = null
    private val binding get() = _binding!!
    private lateinit var networkListener: NetworkListener
    var searchRecipesView: SearchView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        foodViewModel = ViewModelProvider(requireActivity())[FoodViewModel::class.java]
        recipesViewModel = ViewModelProvider(requireActivity())[RecipesViewModel::class.java]
    }

    override fun onCreateView( inflater: LayoutInflater,  container: ViewGroup?, savedInstanceState: Bundle? ): View {
        _binding = FragmentRecipesBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this // bcoz live data is use
        binding.foodViewModel = foodViewModel

        setupRecycler()

        recipesViewModel.readBackOnline.observe(viewLifecycleOwner) {
            recipesViewModel.backOnline = it
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                networkListener = NetworkListener()
                networkListener.checkNetworkAvailability(requireContext())
                    .collect {status ->
                        Log.d("NetworkListener", status.toString())
                        recipesViewModel.networkStatus = status
                        recipesViewModel.showNetworkStatus()
                        readFromDatabase()
                    }
            }
        }

        binding.fabRecipe.setOnClickListener {
            if (recipesViewModel.networkStatus) {
                findNavController().navigate(R.id.action_recipesFragment_to_recipesBottomSheet)
            } else {
                recipesViewModel.showNetworkStatus()
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if (foodViewModel.recyclerViewState != null) {
            binding.recyclerRecipe.layoutManager?.onRestoreInstanceState(foodViewModel.recyclerViewState)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val menuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider{
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                return menuInflater.inflate(R.menu.recipes_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when(menuItem.itemId) {
                    R.id.menu_search -> {
                        setupSearchView(menuItem)
                        searchRecipesView?.setOnQueryTextListener(object : OnQueryTextListener{
                            override fun onQueryTextSubmit(query: String?): Boolean {
                                searchApiData(query.toString())
                                return true
                            }

                            override fun onQueryTextChange(newText: String?): Boolean {
                                print(newText)
                                return true
                            }

                        })
                    }
                }
                return true
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setupSearchView(menuItem: MenuItem) {
        searchRecipesView = menuItem.actionView as SearchView
        searchRecipesView?.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
                ?.setColorFilter(ContextCompat.getColor(requireContext(), R.color.white))
        searchRecipesView?.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
            ?.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        searchRecipesView?.queryHint = "Search Recipes"
    }

    override fun onDestroy() {
        super.onDestroy()
        foodViewModel.recyclerViewState = binding.recyclerRecipe.layoutManager?.onSaveInstanceState()
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
                if (database.isNotEmpty() && !args.backFromBottomSheet || database.isNotEmpty() && isDataRequested) {
                    Log.d("RecipeFragment", "From Database")
                    recipesAdapter.setData(database[0].foodRecipe)
                    hideShimmerEffect()
                } else {
                    if (!isDataRequested){
                        requestApiData()
                        isDataRequested = true
                    }
                }
            }
        }
    }

    private fun searchApiData(searchQuery: String){
        showShimmerEffect()
        foodViewModel.searchRecipes(recipesViewModel.applySearchQuery(searchQuery))
        foodViewModel.searchResponse.observe(viewLifecycleOwner){response ->
            when (response) {
                is NetworkResult.Success -> {
                    hideShimmerEffect()
                    val foodRecipes = response.data
                    foodRecipes?.let { recipesAdapter.setData(it) }
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

    private fun requestApiData() {
        Log.d("RecipeFragment", "From API call")
        foodViewModel.getRecipes(recipesViewModel.applyQueries())
        foodViewModel.recipeResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    hideShimmerEffect()
                    response.data?.let { recipesAdapter.setData(it) }
                    recipesViewModel.saveMealAndDietType()
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