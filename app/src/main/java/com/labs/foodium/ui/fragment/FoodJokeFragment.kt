package com.labs.foodium.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.labs.foodium.R
import com.labs.foodium.databinding.FragmentJokeBinding
import com.labs.foodium.utils.Constants.Companion.API_KEY
import com.labs.foodium.utils.NetworkResult
import com.labs.foodium.viewmodel.FoodViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FoodJokeFragment: Fragment() {

    private val foodViewModel by viewModels<FoodViewModel>()
    private var _binding: FragmentJokeBinding? = null
    private val binding get() =  _binding!!

    private var  foodJoke = "No Food Joke"


    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_joke, container, false)
        binding.lifecycleOwner = this
        binding.foodViewModel = foodViewModel

        foodViewModel.getFoodJoke(API_KEY)
        foodViewModel.foodJokeResponse.observe(viewLifecycleOwner){ response ->
            when(response) {
                is NetworkResult.Success -> {
                    binding.foodJokeTextView.text = response.data?.text
                    if (response.data != null) {
                        foodJoke = response.data.text.toString()
                    }
                }
                is NetworkResult.Error -> {
                    loadDataFromCache()
                    Toast.makeText(requireContext(), response.message.toString(), Toast.LENGTH_SHORT).show()
                }
                is NetworkResult.Loading -> {
                    Log.d("FoodJoke Fragment", "Loading")
                }
            }

        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val menuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                return menuInflater.inflate(R.menu.food_joke_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when(menuItem.itemId) {
                    R.id.share_food_joke_menu -> {
                       val shareIntent =  Intent().apply {
                           this.action = Intent.ACTION_SEND
                           this.putExtra(Intent.EXTRA_TEXT, foodJoke)
                           this.type = "text/plain"
                       }
                        startActivity(shareIntent)
                    }
                }
                return true
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun loadDataFromCache() {
        lifecycleScope.launch {
            foodViewModel.readFoodJoke.observe(viewLifecycleOwner) { database ->
                if (!database.isNullOrEmpty()) {
                    binding.foodJokeTextView.text = database[0].foodJoke.text
                    foodJoke = database[0].foodJoke.text
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}