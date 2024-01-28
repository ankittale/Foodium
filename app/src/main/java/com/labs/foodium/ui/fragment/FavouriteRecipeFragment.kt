package com.labs.foodium.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.labs.foodium.R
import com.labs.foodium.adapter.FavoriteRecipeAdapter
import com.labs.foodium.databinding.FragmentFavouriteBinding
import com.labs.foodium.viewmodel.FoodViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavouriteRecipeFragment: Fragment() {

    private val foodViewModel: FoodViewModel by viewModels()
    private val favoriteAdapter: FavoriteRecipeAdapter by lazy { FavoriteRecipeAdapter(requireActivity(), foodViewModel) }
    private var  _binding: FragmentFavouriteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView( inflater: LayoutInflater,
                               container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        _binding = FragmentFavouriteBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.foodViewModel = foodViewModel
        setupRecyclerView()
        foodViewModel.readFavouriteRecipes.observe(viewLifecycleOwner) { favoriteEntity ->
            favoriteAdapter.setData(favoriteEntity)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val menuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                return menuInflater.inflate(R.menu.favorite_recipe_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when(menuItem.itemId) {
                    R.id.deleteAll_favorite_recipes_menu -> {
                        foodViewModel.readFavouriteRecipes.observe(viewLifecycleOwner){ favoriteEntity ->
                            if (favoriteEntity.isNotEmpty()){
                                foodViewModel.deleteAllFavouriteRecipes()
                                "All Recipes Removed".showSnackMessage()
                            } else {
                                "No Recipes for deletion".showSnackMessage()
                            }
                        }
                    }
                }
                return true
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setupRecyclerView() {
        binding.favoriteRecipesRecyclerView.adapter = favoriteAdapter
        binding.favoriteRecipesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun String.showSnackMessage() {
        Snackbar.make(binding.root, this, Snackbar.LENGTH_LONG)
            .setAction("Ok") {}
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        favoriteAdapter.clearContextualActionMode()
        _binding = null
    }
}