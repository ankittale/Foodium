package com.labs.foodium.adapter

import android.view.ActionMode
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.labs.foodium.R
import com.labs.foodium.data.db.entity.FavouriteEntity
import com.labs.foodium.databinding.FavoriteRecipeRowLayoutBinding
import com.labs.foodium.ui.fragment.FavouriteRecipeFragmentDirections
import com.labs.foodium.utils.RecipesDiffUtil

class FavoriteRecipeAdapter(private val requireActivity: FragmentActivity): RecyclerView.Adapter<FavoriteRecipeAdapter.FavoriteHolder>(), ActionMode.Callback {

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
        /* Single CLick fragment */
        holder.binding.favoriteRecipesRowLayout.setOnClickListener{
            val action =
                FavouriteRecipeFragmentDirections.actionFavouriteRecipeFragmentToDetailsActivity(selectedRecipes.result)
            holder.itemView.findNavController().navigate(action)
        }
        /* Long CLick fragment */
        holder.binding.favoriteRecipesRowLayout.setOnLongClickListener {
            requireActivity.startActionMode(this)
            true
        }

    }

    fun setData(newFavoriteRecipe: List<FavouriteEntity>){
        val recipeDiffUtil = RecipesDiffUtil(favoriteRecipes, newFavoriteRecipe)
        val diffUtil = DiffUtil.calculateDiff(recipeDiffUtil)
        favoriteRecipes = newFavoriteRecipe
        diffUtil.dispatchUpdatesTo(this)
    }

    override fun onCreateActionMode(actionMode: ActionMode?, menu: Menu?): Boolean {
        actionMode?.menuInflater?.inflate(R.menu.favorite_contextual_menu, menu)
        return true
    }

    override fun onPrepareActionMode(actionMode: ActionMode?, menu: Menu?): Boolean {
        return true
    }

    override fun onActionItemClicked(actionMode: ActionMode?, item: MenuItem?): Boolean {
        return true
    }

    override fun onDestroyActionMode(actionMode: ActionMode?) {

    }
}