package com.labs.foodium.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.labs.foodium.databinding.RecipesRowLayoutBinding
import com.labs.foodium.models.FoodRecipe
import com.labs.foodium.models.Result
import com.labs.foodium.utils.RecipesDiffUtil

class RecipesAdapter: RecyclerView.Adapter<RecipesAdapter.RecipesViewHolder>() {

    //Created empty list to set data from api
    private var recipes = emptyList<Result>()

    class RecipesViewHolder(private val binding: RecipesRowLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(result: Result){
            binding.result = result
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): RecipesViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RecipesRowLayoutBinding.inflate(layoutInflater, parent, false)
                return RecipesViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipesViewHolder {
        return RecipesViewHolder.from(parent)
    }

    override fun getItemCount(): Int {
        return recipes.size
    }

    override fun onBindViewHolder(holder: RecipesViewHolder, position: Int) {
        val currentRecipes = recipes[position]
        holder.bind(currentRecipes)
    }

    //update only those view which are not same
    fun setData(newData: FoodRecipe) {
        val  recipeDiffUtil = RecipesDiffUtil(recipes, newData.results)
        val diffUtilResult = DiffUtil.calculateDiff(recipeDiffUtil)
        recipes = newData.results
        diffUtilResult.dispatchUpdatesTo(this)
    }
}