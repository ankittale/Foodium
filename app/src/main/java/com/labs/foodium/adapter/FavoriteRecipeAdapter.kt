package com.labs.foodium.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.labs.foodium.data.db.entity.FavouriteEntity
import com.labs.foodium.databinding.FavoriteRecipeRowLayoutBinding
import com.labs.foodium.utils.RecipesDiffUtil

class FavoriteRecipeAdapter: RecyclerView.Adapter<FavoriteRecipeAdapter.FavoriteHolder>() {

    private var favoriteRecipes = emptyList<FavouriteEntity>()

    class FavoriteHolder(val binding: FavoriteRecipeRowLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(favouriteEntity: FavouriteEntity) {
            binding.favoriteEntity = favouriteEntity
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): FavoriteHolder {
                val layoutInflater =  LayoutInflater.from(parent.context)
                val binding = FavoriteRecipeRowLayoutBinding.inflate(layoutInflater, parent, false)
                return FavoriteHolder((binding))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteHolder {
        return FavoriteHolder.from(parent)
    }

    override fun getItemCount(): Int {
         return favoriteRecipes.size
    }

    override fun onBindViewHolder(holder: FavoriteHolder, position: Int) {
        val selectedRecipes = favoriteRecipes[position]
        holder.bind(selectedRecipes)
    }

    fun setData(newFavoriteRecipe: List<FavouriteEntity>){
        val recipeDiffUtil = RecipesDiffUtil(favoriteRecipes, newFavoriteRecipe)
        val diffUtil = DiffUtil.calculateDiff(recipeDiffUtil)
        favoriteRecipes = newFavoriteRecipe
        diffUtil.dispatchUpdatesTo(this)
    }
}