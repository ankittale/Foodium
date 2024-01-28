package com.labs.foodium.ui.fragment

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.labs.foodium.R
import com.labs.foodium.adapter.IngredientsAdapter
import com.labs.foodium.adapter.RecipesAdapter
import com.labs.foodium.databinding.FragmentIngredientsBinding
import com.labs.foodium.databinding.FragmentOverviewBinding
import com.labs.foodium.models.Result
import com.labs.foodium.utils.Constants
import com.labs.foodium.utils.Constants.Companion.RECIPES_DETAILS


class IngredientsFragment : Fragment() {

    private var ingredientDetails: Result? = null
    private val ingredientsAdapter by lazy { IngredientsAdapter() }

    private var _ingredientsBinding: FragmentIngredientsBinding? = null
    private val binding get() = _ingredientsBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ingredientDetails = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(RECIPES_DETAILS, Result::class.java)
        } else {
            arguments?.getParcelable(RECIPES_DETAILS)
        }
    }

    override fun onCreateView( inflater: LayoutInflater,  container: ViewGroup?, savedInstanceState: Bundle? ): View {
        _ingredientsBinding = FragmentIngredientsBinding.inflate(inflater, container, false)
        setupRecyclerView()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        ingredientDetails?.extendedIngredients?.let {
            ingredientsAdapter.setData(it)
        }
    }

    private fun setupRecyclerView() {
        binding.ingredientsRecyclerview.adapter = ingredientsAdapter
        binding.ingredientsRecyclerview.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onDestroy() {
        super.onDestroy()
        _ingredientsBinding = null
    }
}