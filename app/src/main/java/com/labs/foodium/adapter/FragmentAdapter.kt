package com.labs.foodium.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.labs.foodium.models.Result
import com.labs.foodium.ui.fragment.IngredientsFragment
import com.labs.foodium.ui.fragment.InstructionsFragment
import com.labs.foodium.ui.fragment.OverviewFragment
import com.labs.foodium.utils.Constants.Companion.RECIPES_DETAILS


class ViewPagerFragmentAdapter( fragmentManager: FragmentManager, lifecycle: Lifecycle,
                                resultRecipes: Result ) : FragmentStateAdapter(fragmentManager, lifecycle) {

    private val recipeToSend = resultRecipes
    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> {
                val overviewFragment = OverviewFragment()
                val argsBundle = Bundle()
                argsBundle.putParcelable(RECIPES_DETAILS, recipeToSend)
                overviewFragment.arguments = argsBundle
                return overviewFragment
            }
            1 -> {
                val ingredientsFragment = IngredientsFragment()
                val argsBundle = Bundle()
                argsBundle.putParcelable(RECIPES_DETAILS, recipeToSend)
                ingredientsFragment.arguments = argsBundle
                return ingredientsFragment
            }
            2 ->  {
                val instructionsFragment = InstructionsFragment()
                val argsBundle = Bundle()
                argsBundle.putParcelable(RECIPES_DETAILS, recipeToSend)
                instructionsFragment.arguments = argsBundle
                return instructionsFragment
            }
            else -> {
                val overviewFragment = OverviewFragment()
                val argsBundle = Bundle()
                argsBundle.putParcelable(RECIPES_DETAILS, recipeToSend)
                overviewFragment.arguments = argsBundle
                return overviewFragment
            }
        }
    }

    override fun getItemCount(): Int {
        return 3
    }
}